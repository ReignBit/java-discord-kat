# API
API v2 using MySQL

## Database

http://api.reign-network.co.uk/v3/\<botid>/...

## Endpoints
 - /servers/
    - /servers/\<serverid>
    - /servers/\<serverid>/config/
    - /servers/\<serverid>/\<userid>
    - /servers/\<serverid>/permissions/
    - /

|methods|endpoint|return|
|---|---|---|
|GET|/servers/| List[ids]
|DELETE|/servers/0000| 204
|POST|/servers/| 201

|GET, POST|/servers/00000/config|  Json[Config]
|GET, POST|/servers/00000/permissions| Json[PermissionsAll]
|GET, POST|/flags/| Json[PermissionFlags]


# Flags GET
```
{
    [
        'all',
        'admin',
        'misc',
        ...
    ]
}
```

# Flags POST Body
```
{
    'category': 'all' / 'admin' / ...
}
```


# Flags
```
{

}
```

# Config
```
{

}
```


# PermissionsAll
```
{
    [
        {
            'category' : 'admin',
            'perms': [
                {
                    'user_id': 1230987123987123,
                    'flag': 65184
                },
                ....
            ]
        },
        ...
    ]
}
```