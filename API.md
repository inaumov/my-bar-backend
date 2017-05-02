# REST API documentation

## Index
----
1. Ingredients API
2. Shelf/Bottles API
3. Menu API
4. Cocktails API

## APIs
----

### Ingredients API

#### Show Ingredients

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
  ```json
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
  * **Code:** 204 NO CONTENT <br/> 


### Shelf/Bottles API

#### Show all bottles

Returns json data about bottles of alcoholic beverages that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles

* **Method:**

  `GET`
  
* **Success Response:**

  * **Code:** 200 <br/>
  * **Content:**
  ```json
  [
    {
      "id": 15,
      "ingredient": {
        "id": 3
      },
      "brandName": "Grand Marnier",
      "volume": 0.7,
      "price": 699,
      "inShelf": "YES",
      "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
    }
  ]
  ```

#### Show selected bottle

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
  ```json
  {
    "id": 15,
    "ingredient": {
      "id": 3
    },
    "brandName": "Grand Marnier",
    "volume": 0.7,
    "price": 699,
    "inShelf": "NO",
    "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
  }
  ```

* **Error Response:**
  
  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```json
  {
    "error": "Bottle doesn't exist"
  }
  ```

#### Add a new bottle

Posts json data about new bottle of alcoholic beverage that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles

* **Method:**

  `POST`
  
* **Data Params**
  ```json
  {
    "ingredient": {
      "id": 3
    },
    "brandName": "Grand Marnier",
    "volume": 0.7,
    "price": 699,
    "inShelf": "YES",
    "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
  }
  ```

* **Success Response:**

  * **Code:** 201 CREATED <br/>
  * **Content:**
  ```json
  {
    "id": 98,
    "ingredient": {
      "id": 3
    },
    "brandName": "Grand Marnier",
    "volume": 0.7,
    "price": 699,
    "inShelf": "YES",
    "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2011/03/grand-orange-collins.jpg"
  }
  ```

* **Error Response:**
  
  * **Code:** 422 ENTITY ALREADY EXISTS <br/> 
  * **Content:**
  ```json
  {
    "error": "Bottle already exists"
  }
  ```

#### Update selected bottle

Updates json data about selected bottle of alcoholic beverage that you own in your cupboard, fridge, shelf etc...

* **URL**

  /shelf/bottles

* **Method:**

  `PUT`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Data Params**
  ```json
  {
    "id": 15,
    "ingredient": {
      "id": 3
    },
    "brandName": "Merry Widows Muskat Ottonel 2008",
    "volume": 0.75,
    "price": 275,
    "inShelf": "NO",
    "imageUrl": "https://quentinsadler.files.wordpress.com/2010/06/8wines-043043.jpg"
  }
  ```

* **Success Response:**

  * **Code:** 200 <br/>
  * **Content:**
  ```json
  {
    "id": 15,
    "ingredient": {
      "id": 3
    },
    "brandName": "Merry Widows Muskat Ottonel 2008",
    "volume": 0.75,
    "price": 275,
    "inShelf": "NO",
    "imageUrl": "https://quentinsadler.files.wordpress.com/2010/06/8wines-043043.jpg"
  }
  ```
  
* **Error Response:**
  
  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```json
  {
    "error": "Bottle doesn't exist"
  }
  ```

#### Delete selected bottle

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

#### Delete all bottles

  Deletes all existed bottles of alcoholic beverage that you own in your cupboard, fridge, shelf etc.

* **URL**

  /shelf/bottles

* **Method:**

  `DELETE`
  
* **Success Response:**

  * **Code:** 204 NO CONTENT <br/>

### Menu API

#### Show menu items

  Returns json data about basic menu items (Shot, Long, Smoothie).

* **URL**

  /menu

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```json
  [
    {
      "name": "shot",
      "translation": "Shot"
    },
    {
      "name": "long",
      "translation": "Long"
    }
  ]
  ```  
  * **Code:** 204 NO CONTENT <br />

### Cocktails API

#### Show cocktails

Returns json data about cocktail list in chosen menu.

* **URL**

  /cocktails

* **Method:**

  `GET`

* **URL Params**

  **Optional:**
 
  `filter=[menu]`

  Cocktails can be filtered by menu name.

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```json
  {
    "long": [
      {
        "id": 5,
        "name": "Long Island Iced Tea",
        "available": "NO",
        "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2013/07/Long-Island-Iced-Tea.jpg"
      },
      {
        "id": 10,
        "name": "Margarita",
        "available": "YES",
        "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2013/02/Margarita-new.jpg"
      },
      {
        "id": 20,
        "name": "Sex on the beach",
        "available": "UNDEFINED",
        "imageUrl": null
      }
    ],
    "shot": [
      ...
    ]
  }
  ```

#### Show selected cocktail

Returns json data with recipe details about selected cocktails.

* **URL**

  /cocktails/:id

* **Method:**

  `GET`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Success Response:**

  * **Code:** 200 <br />
  * **Content:**
  ```json
  {
    "id": 5,
    "name": "Long Island Iced Tea",
    "relatedToMenu": "long",
    "available": "NO",
    "imageUrl": "http://liquor.s3.amazonaws.com/wp-content/uploads/2013/07/Long-Island-Iced-Tea.jpg",
    "ingredients": {
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
  ```json
  {
    "error": "Cocktail doesn't exist"
  }
  ```

#### Update selected cocktail

Updates json data about selected cocktail with recipe details.

* **URL**

  /cocktails

* **Method:**

  `PUT`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Data Params**
  ```json
  {
    "id": 1,
    "name": "B52",
    "relatedToMenu": "shot",
    "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
    "ingredients": {
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
  ```json
  {
    "id": 1,
    "name": "B52",
    "relatedToMenu": "shot",
    "available": "NO",
    "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
    "ingredients": {
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
  ```json
  {
    "error": "Cocktail doesn't exist"
  }
  ```

#### Delete selected cocktail

Deletes selected cocktail and related data.

* **URL**

  /cocktails/:id

* **Method:**

  `DELETE`
  
* **PATH Params**

  **Required:**

  `id=[integer]`

* **Success Response:**

  * **Code:** 204 NO CONTENT <br/>

* **Error Response:**

  * **Code:** 404 NOT FOUND <br/> 
  * **Content:**
  ```json
  {
    "error": "Cocktail doesn't exist"
  }
  ```

#### Add new cocktail

Posts json data about selected cocktail with recipe details.

* **URL**

  /cocktails

* **Method:**

  `POST`
  
* **Data Params**
  ```json
  {
    "name": "B52",
    "relatedToMenu": "shot",
    "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
    "ingredients": {
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
  ```json
  {
    "id": 1,
    "name": "B52",
    "relatedToMenu": "shot",
    "imageUrl": "http://www.allcocktails.net/gallery/b-52-cocktail/b52-cocktail.png",
    "available": "NO",
    "ingredients": {
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
  ```json
  {
    "error": "Cocktail already exists"
  }
  ```
