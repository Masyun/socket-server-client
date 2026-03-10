# CLiChat

A TCP socket-based client-server chat application built with pure Java — no frameworks, no dependencies. Features direct messaging, group chat with admin controls, and asynchronous file transfer over a dedicated socket channel.

## Software Architecture

### System Overview

```mermaid
graph TB
    subgraph "Client Process"
        UI[User Input<br/>stdin/Scanner]
        CD[ClientDispatcher<br/>Communicator]
        CR[ClientReceptor<br/>Communicator]
        FI_C[FileInitiator]
    end

    subgraph "Server Process"
        subgraph "Per-Client Thread Pair"
            SR[ServerReceptor<br/>Communicator]
            SD[ServerDispatcher<br/>Communicator]
            FS[FileStorer]
            FI_S[FileInitiator]
        end
        DB[(Database<br/>Singleton)]
    end

    UI -->|user types command| CD
    CD ==>|"TCP :1337<br/>Command Socket"| SR
    SD ==>|"TCP :1337<br/>Command Socket"| CR
    CR -->|print to stdout| UI

    FI_C -.->|"TCP :1338<br/>File Socket"| FS
    FI_S -.->|"TCP :1338<br/>File Socket"| CR

    SR --> DB
    SD --> DB
```

### Design Patterns & Class Hierarchy

```mermaid
classDiagram
    class Component {
        <<abstract>>
        +start() final
        #initialize()*
        #run()*
        #end()*
    }
    note for Component "Template Method Pattern\nstart() enforces lifecycle:\ninitialize() → run() → end()"

    class Publisher {
        <<interface>>
        +addSubscriber()
        +removeSubscriber()
        +notifySubscribers()
    }

    class EventManager {
        -listeners: Map~String, CommandListener~
        +addSubscriber(prefix, operation, listener)
        +notifySubscribers(operation, payload)
    }

    class CommandListener {
        <<abstract>>
        #command: String
        #resCommand: String
        #params: String
        #description: String
        +update(Payload)*
    }
    note for CommandListener "Self-documenting:\neach listener carries its own\nparams + description for\ndynamic /help generation"

    class Communicator {
        <<abstract>>
        -socket: Socket
        -fileTransferSocket: Socket
        -events: EventManager
        #attachListeners()*
        #addListener(command, listener)
        #notifySub(command, payload)
    }

    class Payload~T~ {
        -content: T
        +get(): T
    }
    note for Payload "Generic type-safe\nmessage container"

    class GenericSender {
        -out: PrintWriter
        +update(payload)
    }

    class GenericReceiver {
        +update(payload)
    }

    class Database {
        -instance: Database$
        -users: Vector~User~
        -groups: Vector~Group~
        +getInstance()$
    }
    note for Database "Singleton Pattern\nvolatile + synchronized\nfor thread safety"

    Publisher <|.. EventManager
    CommandListener <|-- GenericSender
    CommandListener <|-- GenericReceiver
    Component <|-- Client
    Component <|-- Server
    Communicator <|-- ClientDispatcher
    Communicator <|-- ClientReceptor
    Communicator <|-- ServerDispatcher
    Communicator <|-- ServerReceptor
    Communicator *-- EventManager : owns
    EventManager o-- CommandListener : routes to
    Communicator ..> Payload : processes
    Runnable <|.. Communicator
```

### Command Processing Flow

```mermaid
sequenceDiagram
    participant User as User (stdin)
    participant CD as ClientDispatcher
    participant EM_C as EventManager
    participant CL as CommandListener<br/>(GenericSender)
    participant Net as TCP :1337
    participant SR as ServerReceptor
    participant EM_S as EventManager
    participant Handler as CommandListener<br/>(Anonymous impl)
    participant DB as Database

    User->>CD: "/dm alice hello"
    CD->>CD: parse → command="/dm"<br/>payload="alice hello"
    CD->>EM_C: notifySub("/dm", payload)
    EM_C->>CL: update(payload)
    CL->>Net: println("/dm alice hello")

    Net->>SR: readLine()
    SR->>SR: parse → command="/dm"<br/>payload="alice hello"
    SR->>EM_S: notifySub("/dm", payload)
    EM_S->>Handler: update(payload)
    Handler->>DB: getUser("alice")
    DB-->>Handler: alice's Socket
    Handler->>Net: write to alice's socket<br/>"/dm_res sender: hello"
```

### Threading Model

```mermaid
graph LR
    subgraph "Server (main thread)"
        Accept["serverSocket.accept() loop"]
    end

    subgraph "Client A Threads"
        SR_A["ServerReceptor A<br/>(Thread)"]
        SD_A["ServerDispatcher A<br/>(Thread)"]
    end

    subgraph "Client B Threads"
        SR_B["ServerReceptor B<br/>(Thread)"]
        SD_B["ServerDispatcher B<br/>(Thread)"]
    end

    subgraph "Each Client Process"
        CDT["ClientDispatcher<br/>(Thread: reads stdin)"]
        CRT["ClientReceptor<br/>(Thread: reads socket)"]
    end

    Accept -->|"new connection"| SR_A
    Accept -->|"new connection"| SD_A
    Accept -->|"new connection"| SR_B
    Accept -->|"new connection"| SD_B

    SR_A -.->|"cross-client DM/group msg"| SD_B
```

### Key Architectural Decisions

| Decision | Rationale |
| --- | --- |
| **Dual-socket architecture** (`:1337` + `:1338`) | File transfers don't block the command/message channel — a large upload won't freeze chat |
| **Template Method** on `Component` | `start()` is `final` — subclasses can't break the lifecycle contract, only fill in the steps |
| **Observer/Publisher** for command dispatch | Adding a new command = adding one listener. No switch statements, no modification of dispatch logic |
| **Dispatcher/Receptor split** per connection | Clean separation: one thread blocks on user input (or socket read), the other blocks on socket write — no interleaving |
| **Generic `Payload<T>`** | Type-safe message passing without casting; serializable for potential future wire format changes |
| **`volatile` + `synchronized` Singleton** | Thread-safe lazy initialization of `Database` without unnecessary locking after first creation |
| **Self-describing `CommandListener`** | Each listener carries `params` and `description` — the `/help` command dynamically generates API docs from registered listeners |
| **`ScheduledExecutorService`** for file transfer timeouts | Async timeout handling without blocking the receptor thread — transfer requests auto-expire |

## Protocol

Commands are newline-delimited strings prefixed with `/`. The server responds with `/<command>_res` by convention.

```text
Client → Server          Server → Client
───────────────          ───────────────
/register user pass      /server Created user ...
/dm alice hello          /dm_res sender: hello  (to alice)
/group_create devs       /server group created
/group_join devs         /server group joined
/group_message devs hi   /group_message_res devs | user: hi  (to all members)
/group_leave devs        /group_leave_res You have left devs
/group_kick devs bob     /group_kick_res user kicked bob from devs
/file_init alice f.txt   /file_init_res ... reply with /file_accept <id>
/file_accept 1           /server f.txt
/logout                  (connection closed)
```
