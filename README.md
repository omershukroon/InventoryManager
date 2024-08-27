# InventoryManager
InventoryManager is application for managing product inventory and orders.
In the application I used Authentication, Realtime Database and Storage.

## Application Flow

### 1. Splash Animation 
When you enter the application, an animation will appear, and after it ends, go to the login screen.

<div class="row">
<img src="https://github.com/user-attachments/assets/e5f49c0f-202b-43f1-85d4-236f9b1dee51" alt="Splash Animation" style="height:700px;"/>
</div>

### 2. Login Screen
Login screen using Authentication from FireBase with login options with email, cell phone and Google account.

<div class="row">
<img src="https://github.com/user-attachments/assets/1d0c899c-7943-4880-9a28-7afb2afb6b69" alt="Login Screen" style="height:700px;"/>
</div>

### 3. Main Screen
On the main screen there is a logout button on the top bar and two buttons,one go to the Product Manager screen and the other to the Order Manager screen.

<div class="row">
<img src="https://github.com/user-attachments/assets/b3e75537-5ebe-41b8-bb78-ddf90f134c33" alt="Main Screen" style="height:700px;"/>
</div>

### 4. Product Manager screen
In the product manager screen in the upper bar there is a button to return to the previous screen and a list of all the user's products that are saved in the Realtime Database.
In addition, there is a search line with which you can search for a product by name and a button to add a new product that, when clicked, goes to the Add Product screen.

<div class="row">
<img src="https://github.com/user-attachments/assets/b0e721f6-28d6-4add-ab34-2154ed78e789" alt="Login Screen" style="height:700px;"/>
</div>

### 5. Add Product screen
In the add product screen, you can add a new product to each of which you can add several pictures through the gallery or the camera And they will save in Storage as soon as the product is added.
In addition, you need to update the product name, price, quantity and barcode after clicking the "Add Product" button, the product will be uploaded to the Realtime Database
And the product will appear in the list on the previous screen. In addition, there is a return button in case you do not want to add the product.

<div class="row">
<img src="https://github.com/user-attachments/assets/c62125aa-0546-4698-a650-c33e63842448" alt="Login Screen" style="height:700px;"/>
</div>

### *Product Manager screen After adding the product*

<div class="row">
<img src="https://github.com/user-attachments/assets/bfef1d71-5829-4174-b86d-a371d75051fc" alt="Login Screen" style="height:700px;"/>
</div>

### 6. Order Manager screen
In the order manager screen, there is a button in the upper bar to return to the previous screen, you can see the list of orders saved in the Realtime Database
Each order has an order number, the amount of the transaction and its status, 
whether it is fulfilled or unfulfilled. In addition, each order card has a color marking for the state of the order, 
and the status of the order can be changed by marking the V on the lower right side. The product quantities are automatically updated in the Realtime Database
According to the status of the order in case the order becomes Fulfilled, the quantities of each product in the order are reduced from the general quantities of each product.
In addition, there is a button for adding an order that goes to the "add order" screen.

<div class="row">
<img src="https://github.com/user-attachments/assets/5a1b6bd3-0f3d-4fe7-b955-f007206336fe" alt="Order Manager screen 1" style="height:700px;"/>
<img src="https://github.com/user-attachments/assets/c7ad84e8-a42b-4c53-9abb-72399d19e21e" alt="Order Manager screen 2" style="height:700px;"/>
</div>


### 5. Add Order screen
On the add order screen, all the products in stock appear and for each product you can choose a quantity to order. 
As soon as you click on "add order" the order number is automatically updated according to the last order entered into the Realtime Database
And it is marked unfulfilled and it is saved in the database with its entire list of products.
In addition, there is a return button if you do not want to add the order.

<div class="row">
<img src="https://github.com/user-attachments/assets/4390bc81-04b4-4142-89a6-d11890e09d65" alt="Add Order screen" style="height:700px;"/>
</div>




