## Download

Download the latest installer from the Releases page.

Garage Mate stores vehicle data locally at:
%LOCALAPPDATA%\GarageMate\garage-data.txt

## GarageMate

The Garage Mate application is to be used for saving and organizing maintenance records for multiple cars and motorcycles. A user has a "garage" in the application, that can contain cars and motorcycles, and each one of those can contain multiple maintenance records.

Garage Mate is a Java application that uses JavaFx to render the GUI, and handle events triggered by user interaction.

At its core, the application uses a .txt file as its database. This .txt file contains all the data to be used by the application. It can render this data for the user to view, update or delete. A user can also create new data in the form of a Vehicle, or a Maintenance record.

Below is the "flow" of the application in use.

When a user opens the program for the first time, the .txt file containing the data should be empty. The dynamic home screen checks if the user has any vehicles and renders a different home screen depending on whether they do or not.

<img width="975" height="607" alt="image" src="https://github.com/user-attachments/assets/dc3e550a-92d7-43ea-a9cb-1e7dd2312fa0" />

In this "empty garage" state, the user has the option to 'Add Vehicle' from the buttons in the footer of the application.

Once a user presses this button, they are taken to a different screen to create a vehicle.  
<img width="975" height="607" alt="image" src="https://github.com/user-attachments/assets/25435f99-e602-45ed-8fb2-6041e81d5886" />

In the 'add vehicle' screen, a form will allow the user to select either "Car" or "Motorcycle". Depending on their choice, the form will update to render different fields, and ultimate save it as a Car or Motorcycle. There is also input validation on these fields to prevent users from saving bad data.

<img width="975" height="606" alt="image" src="https://github.com/user-attachments/assets/402221d4-f568-4ae6-a4aa-3edab9dfb313" />

Once a user saves the vehicle, they are taken back to the home screen. But this time, since there is now data saved to the .txt file, the dynamic home screen now reflects their "garage" as a list of vehicles they can interact with. They can select a card and press the "Remove Selected" button to delete the vehicle, and ultimately any maintenance data tied to that vehicle.

<img width="975" height="607" alt="image" src="https://github.com/user-attachments/assets/50fb1b37-d6fa-4c80-8be1-f3b0c7063ad6" />

They can double click on the cards to enter that respective vehicle "Maintenance history" view, where they can ultimately create or delete maintenance records.

<img width="975" height="608" alt="image" src="https://github.com/user-attachments/assets/85785ddc-ab1d-445e-a81d-918027bb6fea" />

When creating maintenance records, there is some input validation on the input fields. One of them is the mileage field, which can let a user update the vehicles mileage from this screen. This allows the user to save some time and keep data across "tables" in sync.

<img width="1183" height="735" alt="image" src="https://github.com/user-attachments/assets/fc6f255d-a796-4f04-ba00-3f736aa65584" />

Once saved, the user is taken back to the previous screen, where they now have a tabular view of their selected vehicle's maintenance history. The user can interact with the headers in the table to sort the data or resize the columns. From this view they can also delete selected maintenance records.  
<img width="1180" height="735" alt="image" src="https://github.com/user-attachments/assets/bc80dc86-de49-406b-af06-47663ab8f783" />

That is the full user experience for this application at this time.
