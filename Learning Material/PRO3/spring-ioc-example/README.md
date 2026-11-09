# PRO3: Spring IoC and Exercise 4

This folder contains three independent Spring Boot 4.1.1 / Java 25 Maven projects:

| Project | Purpose |
| --- | --- |
| `spring-ioc-example` | Class dependency-injection example: Spring creates a room and injects it into a drink. |
| `GRPCExample/spring-boot-grpc-server-example` | Vinyl library gRPC server, implementing the class handout's AddVinyl and GetVinyl operations. |
| `GRPCExample/spring-boot-grpc-client-example` | Exercise 4: a separate Spring Boot console client replacing BloomRPC. |

The PDFs under `GRPCExample` are preserved. The gRPC implementation follows `03_GetStarted_With_gRPC_v3.pdf` and the [teacher's starter](https://github.com/viajook/spring-boot-grpc-server-example). The client uses the exact `org.springframework.boot:spring-boot-starter-grpc-client` dependency required by `04Exercises-PRO3.pdf`.

## Set up Java in PowerShell

Run these commands in **each terminal** before building or running a project:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
Set-Location 'C:\Users\bilba\pro3\spring-ioc-example'
java -version
```

This installation reports Java 25. The changes affect only the current PowerShell process. Your previous `JAVA_HOME` selected Java 21 even though `java` on PATH selected Java 25, causing Maven's `release version 25 not supported` error.

Each project includes `mvnw.cmd` and `mvnw`; a separate Maven installation is unnecessary. The first build needs internet access to download dependencies. There is no Maven POM at the workspace root; build inside each project or use `-f` as below.

## Build and test all three projects

From the workspace root after setting Java:

```powershell
.\spring-ioc-example\mvnw.cmd -f .\spring-ioc-example\pom.xml clean verify
if ($LASTEXITCODE -ne 0) { throw 'IoC build failed' }
.\GRPCExample\spring-boot-grpc-server-example\mvnw.cmd -f .\GRPCExample\spring-boot-grpc-server-example\pom.xml clean verify
if ($LASTEXITCODE -ne 0) { throw 'Server build failed' }
.\GRPCExample\spring-boot-grpc-client-example\mvnw.cmd -f .\GRPCExample\spring-boot-grpc-client-example\pom.xml clean verify
if ($LASTEXITCODE -ne 0) { throw 'Client build failed' }
```

Protobuf generation is part of the Maven build. Generated Java goes into each project's `target/generated-sources/protobuf`; do not edit generated classes. Both projects contain the same `src/main/proto/vinyl.proto`. When changing the contract, update both copies and rebuild both projects.

## Run the IoC example

```powershell
java -jar .\spring-ioc-example\target\spring-ioc-example-0.0.1-SNAPSHOT.jar
```

After Spring's startup messages, expect:

```text
Drink Object created and injected successfully

Here is your drink
Delivered to room 1 (Demo Room)
```

Override the room values with:

```powershell
java -jar .\spring-ioc-example\target\spring-ioc-example-0.0.1-SNAPSHOT.jar --room.id=42 '--room.name=Classroom'
```

`RoomConfiguration` provides an explicit `@Bean` because `Room(int id, String name)` takes scalar configuration values. Spring supplies this bean through `Drink`'s constructor, which stores it in a final field. The main method retrieves the drink, demonstrates the injected room, and closes the context.

The original code failed because the room call lacked a semicolon, used a different method capitalization, and the drink constructor did not retain the injected room. Component-scanning `Room` would also have required nonexistent `int` and `String` beans. These issues are repaired.

## Run the server and client

**Terminal 1**, from the workspace root with Java configured:

```powershell
java -jar .\GRPCExample\spring-boot-grpc-server-example\target\spring-boot-grpc-server-example-0.0.1-SNAPSHOT.jar
```

Wait for the gRPC server startup message showing port `9090`. Leave this terminal running; stop it with Ctrl+C when finished. The server binds to `127.0.0.1` and uses plaintext gRPC for this local exercise.

**Terminal 2**, from the workspace root with Java configured:

```powershell
$clientJar = '.\GRPCExample\spring-boot-grpc-client-example\target\spring-boot-grpc-client-example-0.0.1-SNAPSHOT.jar'
java -jar $clientJar
$LASTEXITCODE
```

No command runs the demo; `java -jar $clientJar demo` does the same thing. Example output (the UUID varies):

```text
Added vinyl: id=<UUID>, name=Class demo vinyl
Retrieved vinyl: id=<same UUID>, name=Class demo vinyl
Demo successful: retrieved the same ID and name.
```

The client performs two RPCs, checks the returned records match, closes its Spring context and managed channel, and exits with code `0`.

Add a named record, then copy its printed ID into the get command:

```powershell
java -jar $clientJar add '--name=Kind of Blue'
java -jar $clientJar get '--id=PASTE-THE-PRINTED-ID-HERE'
```

Quote the entire `--name=...` argument to preserve spaces in PowerShell. Use the actual ID returned by `add`; names are not IDs. Repeated names are allowed and receive distinct IDs.

For another local port, stop the existing server and launch:

```powershell
# Terminal 1
java -jar .\GRPCExample\spring-boot-grpc-server-example\target\spring-boot-grpc-server-example-0.0.1-SNAPSHOT.jar --spring.grpc.server.port=9091
# Terminal 2
java -jar $clientJar demo --spring.grpc.client.channel.vinyl.target=localhost:9091
```

The same target can be set in the client's `application.properties`. Spring Boot 4.1 uses singular `channel`, not the older `channels` property. Plaintext is configured with `spring.grpc.client.channel.vinyl.ssl.enabled=false`.

To run directly through Maven, enter the corresponding project directory and use `./mvnw.cmd spring-boot:run`; on the client this runs the default demo. Packaged JAR commands above are simpler for command arguments and inspecting the application's exit code.

## Contract and dependency-injection flow

The contract uses package `dk.via.pro3.springbootgrpcexample.generated`, matching the PDF screenshot. The teacher's downloadable starter uses `dk.via.pro3.generated`; both local projects deliberately use the PDF package so their RPC service names match each other.

| RPC | Request | Response |
| --- | --- | --- |
| `AddVinyl` | `AddVinylRequest`: name, field 1 | `VinylResponse`: vinyl, field 1 |
| `GetVinyl` | `GetVinylRequest`: ID, field 1 | `VinylResponse`: vinyl, field 1 |

`Vinyl` contains ID (field 1) and name (field 2). The client service builds requests and calls a generated blocking stub with a fresh five-second deadline on each RPC. Spring creates the named `vinyl` channel and injects the stub into `VinylClientService`, which is injected into the console runner.

On the server, `GrpcVinylServer` extends the generated service base and is registered using Spring's `@GrpcService`. Spring injects the domain `VinylService`, which stores `VinylRecord` objects in a concurrent map. The gRPC adapter converts these domain objects to generated protobuf `Vinyl` messages. This corrects the handout's mixed use of `VinylRecord` and `Vinyl` and completes its missing `getVinyl` implementation.

Storage is **in memory**. All records disappear when the server restarts. The client is a separate application and communicates with the running server over TCP.

## Errors and tests

| Condition | Result |
| --- | --- |
| Successful demo, add, or get | Exit `0` |
| Missing/blank/duplicate name or ID; unknown command/option; extra command | Usage message, exit `2`; no RPC |
| Blank input sent directly to the server | gRPC `INVALID_ARGUMENT` |
| Unknown ID | gRPC `NOT_FOUND`, client exit `1` |
| Server unavailable | gRPC `UNAVAILABLE`, connection guidance, client exit `1` |
| RPC exceeds five seconds | gRPC `DEADLINE_EXCEEDED`, client exit `1` |

Tests cover configured room injection and actual room use; gRPC add/get round trips, unique IDs, blank inputs and unknown IDs; client commands, argument errors, five-second deadline attachment and unavailable-server handling. A client context test exercises the real Spring-managed channel configuration without requiring a running server.

If port 9090 is already occupied, use the 9091 override above. If IntelliJ cannot resolve generated classes, run Maven `compile` and reload the Maven project. The server must stay running while using the client.

## IntelliJ IDEA

1. Open the workspace and add each of the three `pom.xml` files as Maven projects (or open each project separately).
2. Set **Project Structure > Project SDK** and language level to Java 25. The JDK home on this machine is `C:\Program Files\Java`.
3. Set **Settings > Build, Execution, Deployment > Build Tools > Maven > Runner > JRE** to that same Java 25 JDK. Also set the Maven importer JDK to Java 25 and select the Maven wrapper.
4. Reload Maven and run `compile` in both gRPC projects to generate sources.
5. Run `SpringBootGrpcServerExampleApplication` first. Run `SpringBootGrpcClientExampleApplication` next with no program arguments for the demo, or `add --name="Kind of Blue"` / `get --id="..."` as program arguments.
6. Run `SpringIocExampleApplication` separately for the room/drink example.

Reference: [Spring Boot 4.1 gRPC documentation](https://docs.spring.io/spring-boot/reference/io/grpc.html).
