# Frost Authorization Service

* This service extends the Frost server with an upstream component that handles authorization.

## Roles

* Anonymous (de4lPublic)
* Thing Consumer (de4lConsumer)
* Thing Owner (de4lOwner)
* Admin (frost_admin)

## Supported Requests

### Things


| Operation | Thing class | Endpoint    | Admin | Owner | Consumer | Anonymous |
|-----------|-------------|-------------|-------|-------|----------|-----------|
| read      | public      | /Things     | x     | x     | x        | x         |
|           | private     | /Things     | x     | x²    | x²       | -         |
|           | public      | /Things(id) | x     | x     | x        | x         |
|           | private     | /Things(id) | x     | x     | x        | -         |
| update¹   | public      | /Things(id) | x     | x     | -        | -         |
|           | private     | /Things(id) | x     | x     | -        | -         |
| create    | -           | /Things(id) | x     | -     | -        | -         |

### Datastreams

| Operation | Thing class | Endpoint                | Admin | Owner | Consumer | Anonymous |
|-----------|-------------|-------------------------|-------|-------|----------|-----------|
| read      | public      | /Datastreams            | x     | x     | x        | x         |
|           | private     | /Datastreams            | x     | x²    | x²       | -         |
|           | public      | /Datastreams(id)        | x     | x     | x        | x         |
|           | private     | /Datastreams(id)        | x     | x     | x        | -         |
| update¹   | public      | /Datastreams(id)        | x     | x     | -        | -         |
|           | private     | /Datastreams(id)        | x     | x     | -        | -         |
| create    | -           | /Things(id)/Datastreams | x     | -     | -        | -         |

### Observations

| Operation | Thing class | Endpoint                      | Admin | Owner | Consumer | Anonymous |
|-----------|-------------|-------------------------------|-------|-------|----------|-----------|
| read      | public      | /Observations                 | x     | x     | x        | x         |
|           | private     | /Observations                 | x     | x²    | x²       | -         |
|           | public      | /Observations(id)             | x     | x     | x        | x         |
|           | private     | /Observations(id)             | x     | x     | x        | -         |
| update¹   | public      | /Observations(id)             | x     | x     | -        | -         |
|           | private     | /Observations(id)             | x     | x     | -        | -         |
| create    | -           | /Datastreams(id)/Observations | x     | -     | -        | -         |

¹ POST, PATCH, DELETE
² only applies to those things for which the user has the appropriate role (Owner, Consumer)

## Realization

* combination of Keycloak and an extension of the STA model
    * Keycloak: realm role admin (frost_admin)
    * STA model extension: Thing class may include the properties de4lPublic (boolean), de4lConsumer (List of KeycloakIds) and de4lOwner (KeycloakId)
