# MeetApp
MeetApp is a “one stop shop” super-app.
The app allows teams to efficiently organize shared commutes, plan meals, and track shared expenses, making it easy to manage day-to-day errands.
### Super what?
super-app is a mobile application that offers wide range of services and functions within a single platform, making it a one-stop-shop for users.
Super apps are becoming increasingly popular globally as they offer a wide range of services and features, which can be accessed from a single platform, making it more convenient for users.

## Mini apps
### Lift
![image](https://github.com/Noam-Levy/MeetApp/assets/90918336/b573d7f5-d890-4765-81a7-1f60a7e64f5a)

Plan shared commutes with co-workers for a more efficient and cost-effective morning.

### Grab
![image](https://github.com/Noam-Levy/MeetApp/assets/90918336/a0d4c83e-4089-4b5f-b627-b736036bc0af)

Create a poll for team lunch options and decide on food for a gathering.

### Split
![image](https://github.com/Noam-Levy/MeetApp/assets/90918336/0e21dbca-862e-409f-9d73-eb481c10b482)

Track and share team expenses, maintain a tab of who owes what to whom.

## Scope
this repository hosts the necessary infrastructure to handle all logical operations to fulfil the above services. In the future, a dedicated client might also br developed.

## How to use
The application expects some enivronment variables to be present in order to fully operate:
  1. ACTIVE_PROFILE - mandates the required spring profile to be loaded:
  
      production - expects also APP_DB_HOST, APP_DB_NAME, APP_DB_USERNAME, APP_DB_PASSWORD and APP_DB_PORT to be initialized in order to log in a MySQL production DB.
    
      staging - expects also APP_DB_HOST, APP_DB_TEST_NAME, APP_DB_USERNAME, APP_DB_PASSWORD and APP_DB_PORT to be initialized in order to log in a MySQL DB.
    
      testing - will initialize a H2 instance as a DB (this means that runtime data saved to the database is non-presistant) MeetApp will default to this environment
    
  2. APP_DB_HOST - MySQL DB address
  
  3. APP_DB_NAME - MySQL production DB name
  
  4. APP_TEST_DB_NAME - MySQL testing DB name
  
  5. APP_DB_USERNAME - DB username
  
  6. APP_DB_PASSWORD - DB username
  
  7. APP_DB_PORT - MySQL db port
  
  8. MAP_BOX_API_KEY - MeetApp uses [MapBox API](https://docs.mapbox.com/api/overview/) with grab and lift and requires an API key for the services to work.

Obviously, you will also need to run gradle build in order to have all necessary dependencies installed.
