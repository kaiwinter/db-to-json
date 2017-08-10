# db-to-json
Exports any database (complete or single tables) as JSON. Tested with SQLite, should work with other databases as well.

## Example
### Database

table invoice

id  | address
--- | ---------
1   | Address A
2   | Address B


table user

id  | username
--- | ---------
1   | User A
2   | User B

### Config
	{
	    "jdbcDriver": "org.sqlite.JDBC",
	    "connectionString": "jdbc:sqlite:/test-sqlite.db",
	    "user": "root",
	    "password": "",
	    "query": "SELECT id, username FROM user",
	    "pretty": true,
	    "underscoreToCamelcase": true
	}
	
### Result
	{
	   "SELECT id, username FROM user": [
	      {
	         "id": 1,
	         "username": "User A"
	      },
	      {
	         "id": 2,
	         "username": "User B"
	      }
	   ]
	}

To export all tables at once leave the query empty:

	[
	   {
	      "user": [
	         {
	            "id": 1,
	            "username": "User A"
	         },
	         {
	            "id": 2,
	            "username": "User B"
	         }
	      ]
	   },
	   {
	      "invoice": [
	         {
	            "id": 1,
	            "address": "Address A"
	         },
	         {
	            "id": 2,
	            "address": "Address B"
	         }
	      ]
	   }
	]
	
### Run

Main -config config.json [-outfile dump.json]