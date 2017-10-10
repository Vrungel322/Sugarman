package com.sugarman.myb.api.models.responses;

import java.util.List;

/**
 * Created by nikita on 10.10.2017.
 */

public class ShopProduct {
  private String imgUrl;
  private List<String> imgDetailUrls;
  private String productName;
  private String productPrice;

  public ShopProduct(String imgUrl, List<String> imgDetailUrls, String productName,
      String productPrice) {
    this.imgUrl = imgUrl;
    this.imgDetailUrls = imgDetailUrls;
    this.productName = productName;
    this.productPrice = productPrice;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public List<String> getImgDetailUrls() {
    return imgDetailUrls;
  }

  public void setImgDetailUrls(List<String> imgDetailUrls) {
    this.imgDetailUrls = imgDetailUrls;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductPrice() {
    return productPrice;
  }

  public void setProductPrice(String productPrice) {
    this.productPrice = productPrice;
  }
}