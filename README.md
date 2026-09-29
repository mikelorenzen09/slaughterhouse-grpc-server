# slaughterhouse-grpc-server

gRPC service for the PRO3 course assignment, part 2. It returns:
- the registration numbers of all animals involved in a product
- all products an animal has been involved in

## Structure

```
(SQL scripts are in ../slaughterhouse-grpc-database/database)
src/main/proto/                slaughterhouse.proto - the gRPC contract
src/main/java/.../slaughterhouse/
  SlaughterhouseGrpcServerApplication.java
  grpc/GrpcSlaughterhouseServer.java     gRPC endpoint (TODOs to fill in)
  (add) model/       JPA entities: Animal, Tray, Part, Product
  (add) repository/  Spring Data repositories
  (add) service/     SlaughterhouseService (business logic)
```

## Run

1. Create the database and tables (scripts in `../slaughterhouse-grpc-database/database`).
2. Set username/password in `src/main/resources/application.properties`.
3. Maven > Lifecycle > compile (generates the gRPC classes).
4. Run `SlaughterhouseGrpcServerApplication` - gRPC listens on port 9090.
5. Test in BloomRPC by importing `src/main/proto/slaughterhouse.proto`.
