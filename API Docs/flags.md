# Flags
/flags endpoint

___
## GET /flags
Returns a list of all categories of permission flags.
### Response
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

___
## GET /flags?category=all
Returns flag data for all users in specified category

### Response
```
{
    'category': 'all',
    'flags': [
        {
            'user_id': 12397812308945,
            'value'  : 65474
        },
        ...
    ]
}
```

___
## POST /flags
Create a new permission for a user

### Request
```
{
    'category': 'admin',            -- Category to add permission       *
    'user_id' : 12340872340432,     -- ID of the user.                  *
    'flag'    : 0                   -- Flag to set to. Defaults to none
}
```

### Response
201 Created
```

```

___
## DELETE /flags/\<category>/\<userid>
Delete a permission entry.
### Response
204 No Content
```
```

___
## POST /flags/\<category>/\<userid>
Edit a permission entry
### Request
```
{
    'flag': 1234                    -- Value of new flag *
}
```

### Response
```
{
    'category': 'admin',           
    'user_id' : 12340872340432,
    'flag'    : 1234
}
```