package models;

import java.util.*;
import javax.persistence.*;
import io.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import controllers.*;
import models.products.*;
@Entity
public class Product extends Model {

    @Id
    // @Constraints.Required
    private Long productID;

    @Constraints.Required
    private String productName;

    @Constraints.Required
    private String productDescription;

    @Constraints.Required
    private double productPrice;

    @Constraints.Required
    private int productQty;

    // @Constraints.Required
    private int totalSold;

    // @Constraints.Required
    private double overallRating;

    private double summedRating;

    private int countRating;

    private static final int QTY_LOW = 5; //new
    //constant for the restock notification
    // (i.e., what's the least amount of something that you can have before sending the notification)

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public Product() {
    }

    public Product(Long productID, String productName, String productDescription,
                    double productPrice, int productQty) {
        this.productID = productID;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.totalSold = 0;
        this.overallRating = 0;
        this.summedRating = 0;
        this.countRating = 0;
    }

    //Accessors and mutators / getters and setters
    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold){
        this.totalSold = totalSold;
    }

    public double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(double rating){
        this.overallRating = rating;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Review> getReviews(){
        return reviews;
    }

    public void setReviews(List<Review> reviews){
        this.reviews = reviews;
    }

    public int getCountRating(){
        return this.countRating;
    }
    //end of getters and setters

    //Finder and finder methods
    public static Finder<Long, Product> find = new Finder<>(Product.class);

    public static final List<Product> findAll() {
        return Product.find.all();
    }

    public static Product getProductById(Long id){
        if(id <= 0){
            return null;
        } else {
            return find.query().where().eq("product_id", id).findUnique();
        }
    }
    //End of finder methods


    public void decrementStock(){
        productQty--;
    }

    public void incrementStock(){
        productQty++;
    }

    public void restock(int quantity){
        productQty += quantity;
    }

    public void purchase(int quantity){
        if(productQty < quantity){
            //Display error message
        } else {
            productQty -= quantity;
            totalSold+=quantity;
        }
    }

    public boolean checkLowQty(){
        if(productQty<=QTY_LOW){
            return true;
        }
        return false;
    }

    public void calculateRating(double rating){
        summedRating += rating;
        countRating++;
        overallRating = summedRating / countRating;
    }

    public static List<Product> getLowQty(){
        List<Product> lowQtyProd = new ArrayList<>();
        for(Product e: Product.findAll()){
            if(e.checkLowQty()){
                lowQtyProd.add(e);
            }
        }
        return lowQtyProd;
    }

    public static Map<String,String> options(String s) {
        Map<String,String> options = new LinkedHashMap<>();
        List<ProductSkeleton> plist = ProductController.getSpecs(Long.valueOf(0), "");
        for(ProductSkeleton pl: plist){
            String cat = pl.getProduct().getCategory().getName().toLowerCase();

                if(cat.contains(s)){
                    options.put(pl.getProductId().toString(), pl.getProduct().getProductName());
                }

        }
        return options;
    }

    public boolean checkLengthOfStrings(){
        if(ProductSkeleton.checkStringLen(productName) || ProductSkeleton.checkStringLen(productDescription)){
            return true;
        }
        return false;
    }
}