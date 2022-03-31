# Servers Endpoints

## GET /servers
Returns a list of all server ids
### Response
```
[
    1209812947294,
    3241089423098,
    ...
]
```

___
## POST /servers
Create a new server
### Request
```
{
    'server_id' : 1234089412376,        -- ID of the server *
    'prefix'    : '!'                   -- Prefix to set for the server *
    'owner_id'  : 172408031060033537    -- user id of the owner of the server *
}
```
### Response
201 Created
```
```

___
## GET /servers/\<serverid>
Return data for a server
### Response
```
{
    'server_id' : 2137801237,               
    'owner_id'  : 123089123780312,
    'prefix'    : '!',
    'joined_at' : 123789123098495054    -- Timestamp when the bot joined the server
}
```

___
## DELETE /servers/\<serverid>
Delete a server from the API
### Response
204 No Content
