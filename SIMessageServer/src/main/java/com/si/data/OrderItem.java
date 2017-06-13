/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mingxing Chen
 */
package com.si.data;


import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * OrderItem represent a full name of an order item. For example the food item Tom Yum Soup, size Large, with the choice
 * of Seafood in the soup is a single food item represented as Tom Yum Soup[Large][Seafood].
 * The OrderItem class is created for this purpose
 */
public class OrderItem{
    private SubItem[] subItems;
    private Category itemCategory;
    public static Language language;
    
    private OrderItem(){
        subItems = new SubItem[1];
        subItems[0] = new SubItem();
    }
    
    /**
     * Create OrderItem from item. The item may have parent item and further parent. The OrderItem will
     * trace back to the top parent and take the top one as item and add the rest as a list of subItems
     * @param item
     */
    public OrderItem(Item item){   
        // item may contains lots other contents, we create OrderItem with only name, description and price
        // don't alter any item info
        ArrayList<SubItem> stack = new ArrayList<>();
        SubItem btItem = new SubItem();
        btItem.setName(item.getName());
        btItem.setPrice(item.getPrice());
        btItem.setDescription(item.getDescription());
        stack.add(btItem);   
        Item pItem = item;
        while(pItem.getParent() != null){
            pItem = pItem.getParent();
            btItem = new SubItem();
            btItem.setName(pItem.getName());
            btItem.setPrice(pItem.getPrice());
            btItem.setDescription(pItem.getDescription());
            // add to the top
            stack.add(0, btItem);              
        }
        subItems = new SubItem[stack.size()];
        subItems = stack.toArray(subItems);   

        language = Item.language;
        if(pItem.getCategory() != null){
            // create a simple category
            itemCategory = new Category();
            itemCategory.setName(pItem.getCategory().getName());
            itemCategory.setDescription(pItem.getCategory().getDescription());
        } 
    }
    
    /*
    * Create new OrderItem with a JSONOb String, the String must be a valid json string
    * @param valid json string
    */ 
    public OrderItem(String jsonString){   
        this(new JSONObject(jsonString));
    }
    
    public OrderItem(JSONObject obj){
        Object object = obj.get("item");
        
        if(object instanceof JSONArray){
            JSONArray arr = obj.optJSONArray("item");
            subItems = new SubItem[arr.length()];
            for(int i=0; i<arr.length(); i++)
                subItems[i] = new SubItem(arr.optJSONObject(i));
        }else if(object instanceof JSONObject){
            subItems = new SubItem[1];
            subItems[0] = new SubItem(obj.optJSONObject("item"));
        }else if(object instanceof String){
            System.out.println("item is string");
            subItems = new SubItem[1];
            subItems[0] = new SubItem(new JSONObject((String)object));
        }else{ // create with null
            subItems = new SubItem[1];
            SubItem subItem = new SubItem();
            subItem.setName(new JSONObject());
            subItem.setDescription("");
            subItem.setPrice("");
            subItems[0] = subItem;
        }
        
        if(obj.has("item_category")){
            itemCategory = new Category( obj.optJSONObject("item_category") );
        }
    }
    
    /*
    * Get OrderItem Name with default language
    */
    public String getDisplayName(){
        String name = subItems[0].getDisplayName();
        for(int i=1; i<subItems.length; i++)
            name += "[" + subItems[i].getDisplayName() + "]";
        return name;       
    }     
    
    /**
     * Get Name with language name, which is used in JSON as key
     * @param lanName, such as "zh", "en"
     * @return 
     */
    public String getName(String lanName){
        String name = subItems[0].getName(lanName);
        for(int i=1; i<subItems.length; i++)
            name += "[" + subItems[i].getName(lanName) + "]";
        return name;      
    }
    
    /**
     * Get item's Description
     * @return the description of item. empty string if no description 
     */
    public String getItemDescription(){
        return subItems[0].getDescription();
    }
    
    /**
     * Return the price of the OrderItem
     * @return price
     */
    public BigDecimal getPrice(){
        
        BigDecimal price = new BigDecimal(0.00);
        for(SubItem sbitem:subItems){
            if(sbitem.getPrice() != null && !sbitem.getPrice().isEmpty())
                price = price.add(new BigDecimal(sbitem.getPrice())).setScale(2, RoundingMode.HALF_UP);
        }        
        return price;
    }    

    /**
     * The category of the OrderItem
     * @return Category
     */
    public Category getOrderItemCategory(){
        return itemCategory;
    }
    
    /**
     * Set the Category of the OrderItem, only a simple Category  is created and set to the OrderItem
     * @param category 
     */
    public void setOrderItemCategory(Category cat){
        // we only need name and description of the cat
        if(cat != null){
            itemCategory = new Category();
            itemCategory.setName(cat.getName());
            itemCategory.setDescription(cat.getDescription());
        }
    }   
    
    /**
     * To JSONObject representation of this OrderItem
     * @return JSONObject
     */
    public JSONObject toJSONObject(){
        JSONObject obj = new JSONObject();
        
        JSONArray array = new JSONArray();
        for(SubItem item:subItems){
            array.put(item.toJSONObject());
        }

        obj.put("item", array);
        if(itemCategory != null){
            obj.put("item_category", itemCategory.toJSONObject());
        }        
        return obj;
    } 

    @Override
    public String toString(){
        return toJSONObject().toString();
    }
    
    public static void main(String[] arg){
        String jsonStr = "{\"item\": [" +
"          {" +
"            \"name\":{\"en\":\"Tom Yum Soup\", \"zh\":\"湯样湯\"}," +
"            \"description\": \"Spicy and sour herbs in lemongrass broth, mushroom, tomatos, bamboo shoot, pineapple, red bell pepper and scallion\"," +
"            }," +
"            " +
"              {" +
"                \"name\": {\"en\":\"Large\", \"zh\":\"大\"}}," +
"                " +
"                  {" +
"                    \"name\": {\"en\":\"Chicken\", \"zh\":\"鸡肉\"}," +
"                    \"price\": \"6.95\"" +
"                  }," +
"                  {" +
"                    \"name\": {\"en\":\"Vegetable\", \"zh\":\"菜\"}," +
"                    \"price\": \"6.95\"" +
"                  }], \"item_category\":"
                + "{" +
"            \"name\":{\"en\":\"Soup\", \"zh\":\"湯湯\"}," +
"            \"description\": \"scallion\"" +
"            }}";
        System.out.println("JSON String before:" + jsonStr);
        OrderItem item = new OrderItem(jsonStr);
        System.out.println("JSON String after: " + item.toJSONObject().toString());
        OrderItem another = new OrderItem(item.toJSONObject().toString());
        System.out.println("old display: " + item.getDisplayName());
        System.out.println("new display: " + another.getDisplayName());
        System.out.println("order item category is: " + item.getOrderItemCategory());
        
        Item.language = Language.ZH;
        System.out.println("after change language");
        System.out.println("old display: " + item.getDisplayName());
        System.out.println("new display: " + another.getDisplayName());
        System.out.println("price of the item is: " + item.getPrice() );
        System.out.println("description: " + item.getItemDescription());
        System.out.println("order item category is: " + item.getOrderItemCategory().getDisplayName());
    }
}
