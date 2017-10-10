package com.sugarman.myb.api.models.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by nikita on 10.10.2017.
 */

public class ShopProductEntity implements Parcelable {
  @SerializedName("name") private String productName;
  @SerializedName("desc") private String productDescription;
  @SerializedName("price")private String productPrice;
  @SerializedName("images")private List<String> imgDetailUrls;
  @SerializedName("id") private  String id;

  public ShopProductEntity(String id, String productName, String productDescription,
      String productPrice, List<String> imgDetailUrls) {
    this.id = id;
    this.productName = productName;
    this.productDescription = productDescription;
    this.productPrice = productPrice;
    this.imgDetailUrls = imgDetailUrls;
  }

  protected ShopProductEntity(Parcel in) {
    id = in.readString();
    productName = in.readString();
    productDescription = in.readString();
    productPrice = in.readString();
    imgDetailUrls = in.createStringArrayList();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(productName);
    dest.writeString(productDescription);
    dest.writeString(productPrice);
    dest.writeStringList(imgDetailUrls);
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<ShopProductEntity> CREATOR = new Creator<ShopProductEntity>() {
    @Override public ShopProductEntity createFromParcel(Parcel in) {
      return new ShopProductEntity(in);
    }

    @Override public ShopProductEntity[] newArray(int size) {
      return new ShopProductEntity[size];
    }
  };

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getProductPrice() {
    return productPrice;
  }

  public void setProductPrice(String productPrice) {
    this.productPrice = productPrice;
  }

  public List<String> getImgDetailUrls() {
    return imgDetailUrls;
  }

  public void setImgDetailUrls(List<String> imgDetailUrls) {
    this.imgDetailUrls = imgDetailUrls;
  }
}