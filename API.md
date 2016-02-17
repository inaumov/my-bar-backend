#REST API documentation

##Index
----
* Ingredients API
* Shelf/Bottles API
* Menu API
* Cocktails API

##APIs
----

###Ingredients API

####Show Ingredients

Returns json data about known ingredients (Alcoholic Beverages, Non-Alcoholic Drinks and Additives).

* **URL**

  /ingredients

* **Method:**

  `GET`
  
* **URL Params**

  **Optional:**
 
  `filter=[text]`

  Ingredients can be filtered. Three filter values exist in the current implementation: _beverages_, _drinks_ and _additives_.

* **Success Response:**

  * **Code:** 200 <br/>
  * **Content:**
  ```
    {
      "beverages": [
        {
          "id": 5,
          "kind": "Bourbon",
          "beverageType": "DISTILLED"
        }
      ],
      "drinks": [
        {
          "id": 17,
          "drinkType": "SODA",
          "kind": "Coca Cola"
        }
      ],
      "additives": [
        {
          "id": 14,
          "kind": "Ice"
        }
      ]
    }
  ```

* **Error Response:**
  
  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```
    {
      "error" : "Ingredients list has not been found."
    }
  ```

###Shelf/Bottles API

####Show all bottles

Returns json data about bottles of alcoholic beverages that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles

* **Method:**

  `GET`
  
* **Success Response:**

  * **Code:** 200 <br/>
  * **Content:**
  ```
    [
      {
        "id": 15,
        "ingredient": {
          "id": 42
        },
        "brandName": "Grand Marnier",
        "volume": 0.7,
        "price": 699,
        "inShelf": true,
        "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
      }  
    ]
  ```

####Show selected bottle

Returns json data about selected bottle of alcoholic beverage that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles/:id

* **Method:**

  `GET`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```
    {
    	"id": 15,
    	"ingredient": {
    		"id": 42
    	},
    	"brandName": "Grand Marnier",
    	"volume": 0.7,
    	"price": 699,
    	"inShelf": true,
    	"imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
    }
  ```

* **Error Response:**
  
  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```
    {
      "error" : "Bottle doesn't exist"
    }
  ```

####Add a new bottle

Posts json data about new bottle of alcoholic beverage that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles

* **Method:**

  `POST`
  
* **Data Params**
  ```
    {
      "ingredient": {
      	"id": 42
      },
      "brandName": "Grand Marnier",
      "volume": 0.7,
      "price": 699,
      "inShelf": true,
      "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
    }
  ```

* **Success Response:**

  * **Code:** 201 CREATED <br/>
  * **Content:**
  ```
    {
      "id": 98,
      "ingredient": {
        "id": 42
      },
      "brandName": "Grand Marnier",
      "volume": 0.7,
      "price": 699,
      "inShelf": false,
      "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
    }
  ```

* **Error Response:**
  
  * **Code:** 422 ENTITY ALREADY EXISTS <br/> 
  * **Content:**
  ```
    {
      "error" : "Bottle already exists"
    }
  ```

####Update selected bottle

Updates json data about selected bottle of alcoholic beverage that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles/:id

* **Method:**

  `PUT`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Data Params**
  ```
    {
      "id": 15,
    	"ingredient": {
    		"id": 47
    	},
    	"brandName": "Merry Widows Muskat Ottonel 2008",
    	"volume": 0.75,
    	"price": 275,
    	"inShelf": false,
    	"imageUrl": "https://quentinsadler.files.wordpress.com/2010/06/8wines-043043.jpg"
    }
  ```

* **Success Response:**

  * **Code:** 200 <br/>
  * **Content:**
  ```
    {
    	"id": 15,
    	"ingredient": {
    		"id": 47
    	},
    	"brandName": "Merry Widows Muskat Ottonel 2008",
    	"volume": 0.75,
    	"price": 275,
    	"inShelf": false,
    	"imageUrl": "https://quentinsadler.files.wordpress.com/2010/06/8wines-043043.jpg"
    }
  ```
  
* **Error Response:**
  
  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```
    {
      "error" : "Bottle doesn't exist"
    }
  ```

####Delete selected bottle

Deletes selected bottle of alcoholic beverage that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles/:id

* **Method:**

  `DELETE`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Success Response:**

  * **Code:** 204 NO CONTENT <br/>

####Delete all bottles

  Deletes all existed bottles of alcoholic beverage that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles

* **Method:**

  `DELETE`
  
* **Success Response:**

  * **Code:** 204 NO CONTENT <br/>

###Menu API

####Show menu items

  Returns json data about basic menu items (Shot, Long, Smoothie).

* **URL**

  /menu

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```
    [
      {
        "id": 1,
        "name": "Shot"
      },
      {
        "id": 2,
        "name": "Long"
      },
      {
        "id": 3,
        "name": "Smoothie"
      }
    ]
  ```
  
  * **Error Response:**
    
    * **Code:** 404 NOT FOUND <br/> 
    * **Content:**
  ```
    {
      "error" : "Menu list is empty"
    }
  ```
     
###Cocktails API

####Show cocktails for menu

Returns json data about cocktail list in chosen menu.

* **URL**

  /menu/:id/cocktails

* **Method:**

  `GET`
  
* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```
    [
      {
          "id": 5,
          "name": "Long Island Iced Tea",
          "state": "AVAILABLE",
          "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2013/07/Long-Island-Iced-Tea.jpg"
      },
      {
          "id": 10,
          "name": "Margarita",
          "state": "AVAILABLE",
          "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2013/02/Margarita-new.jpg"
      },
      {
          "id": 20,
          "name": "Sex on the beach",
          "state": "AVAILABLE",
          "imageUrl": null
      }
    ]
  ```

####Show selected cocktail

Returns json data with recipe details about selected cocktails.

* **URL**

  /menu/cocktails/:id

* **Method:**

  `GET`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```
    {
      "id": 5,
      "name": "Long Island Iced Tea",
      "menuId": 2,
      "state": "AVAILABLE",
      "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2013/07/Long-Island-Iced-Tea.jpg",
      "insideItems": {
          "beverages": [
              {
                  "ingredientId": 1,
                  "volume": 20,
                  "unitsValue": "ML",
                  "missing": false
              },
              {
                  "ingredientId": 2,
                  "volume": 20,
                  "unitsValue": "ML",
                  "missing": false
              },
              {
                  "ingredientId": 3,
                  "volume": 20,
                  "unitsValue": "ML",
                  "missing": false
              },
              {
                  "ingredientId": 4,
                  "volume": 20,
                  "unitsValue": "ML",
                  "missing": false
              },
              {
                  "ingredientId": 6,
                  "volume": 20,
                  "unitsValue": "ML",
                  "missing": false
              }
          ],
          "additives": [
              {
                  "ingredientId": 14,
                  "volume": 5,
                  "unitsValue": "PCS"
              },
              {
                  "ingredientId": 18,
                  "volume": 5,
                  "unitsValue": "PCS"
              }
          ],
          "drinks": [
              {
                  "ingredientId": 17,
                  "volume": 150,
                  "unitsValue": "ML"
              }
          ]
      },
      "description": "A Long Island Iced Tea is a type of alcoholic mixed drink typically made with, among other ingredients, tequila, vodka, light rum, triple sec, and gin. It is so named because of the resemblance to the color and taste of iced tea."
    }
  ```

* **Error Response:**
  
  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```
    {
      "error" : "Cocktail doesn't exist"
    }
  ```

####Update selected cocktail

Updates json data about selected cocktail with recipe details.

* **URL**

  /menu/cocktails/:id

* **Method:**

  `PUT`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Data Params**
  ```
    {
      "id": 1,
      "name": "B52",
      "menuId": 1,
      "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
      "insideItems": {
        "beverages": [
          {
            "ingredientId": 8,
            "volume": 20
          },
          {
            "ingredientId": 11,
            "volume": 20
          },
          {
            "ingredientId": 16,
            "volume": 20
          }
        ]
      },
      "description": "The B-52 cocktail is a layered shot composed of a coffee liqueur, an Irish cream, and a triple sec. When prepared properly, the ingredients separate into three distinctly visible layers."
    }  
  ```

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```
    {
      "id": 1,
      "name": "B52",
      "menuId": 1,
      "state": "NOT_AVAILABLE",
      "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
      "insideItems": {
        "beverages": [
          {
            "ingredientId": 8,
            "volume": 20,
            "unitsValue": "ML",
            "missing": true
          },
          {
            "ingredientId": 11,
            "volume": 20,
            "unitsValue": "ML",
            "missing": false
          },
          {
            "ingredientId": 16,
            "volume": 20,
            "unitsValue": "ML",
            "missing": true
          }
        ]
      },
      "description": "The B-52 cocktail is a layered shot composed of a coffee liqueur, an Irish cream, and a triple sec. When prepared properly, the ingredients separate into three distinctly visible layers."
    }
  ```

* **Error Response:**

  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```
    {
      "error" : "Cocktail doesn't exist"
    }
  ```

####Delete selected cocktail

Deletes selected cocktail and related data.

* **URL**

  /menu/cocktails/:id

* **Method:**

  `DELETE`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Success Response:**

  * **Code:** 204 NO CONTENT <br/>

####Add a new cocktail

Posts json data about selected cocktail with recipe details.

* **URL**

  /menu/cocktails

* **Method:**

  `POST`
  
* **Data Params**
  ```
    {
      "name": "B52",
      "menuId": 1,
      "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
      "insideItems": {
        "beverages": [
          {
            "ingredientId": 8,
            "volume": 20
          },
          {
            "ingredientId": 11,
            "volume": 20
          },
          {
            "ingredientId": 16,
            "volume": 20
          }
        ]
      },
      "description": "The B-52 cocktail is a layered shot composed of a coffee liqueur, an Irish cream, and a triple sec. When prepared properly, the ingredients separate into three distinctly visible layers."
    }  
  ```

* **Success Response:**

  * **Code:** 201 CREATED <br/>
  * **Content:**
  ```
    {
      "id": 1,
      "name": "B52",
      "menuId": 1,
      "state": "NOT_AVAILABLE",
      "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
      "insideItems": {
        "beverages": [
          {
            "ingredientId": 8,
            "volume": 20,
            "unitsValue": "ML",
            "missing": false
          },
          {
            "ingredientId": 11,
            "volume": 20,
            "unitsValue": "ML",
            "missing": true
          },
          {
            "ingredientId": 16,
            "volume": 20,
            "unitsValue": "ML",
            "missing": true
          }
        ]
      },
      "description": "The B-52 cocktail is a layered shot composed of a coffee liqueur, an Irish cream, and a triple sec. When prepared properly, the ingredients separate into three distinctly visible layers."
    }
  ```

* **Error Response:**
  
  * **Code:** 422 ENTITY ALREADY EXISTS <br/> 
  * **Content:**
  ```
    {
      "error" : "Cocktail already exists"
    }
  ```
