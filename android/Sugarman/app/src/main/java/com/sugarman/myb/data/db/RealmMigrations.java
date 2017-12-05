package com.sugarman.myb.data.db;

import com.sugarman.myb.models.animation.ImageModel;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 18.10.2017.
 */

public class RealmMigrations implements RealmMigration {

  @Override public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
    final RealmSchema schema = realm.getSchema();

    // NOT DELETE - THIS IS SPAAARTA... EXAMPLE

    //migration mechanism
    //LastBookingEntity extends RealmObject
    //oldVersion -> look in DbHelper constructor and newVersion-1
    //if (oldVersion == 2) {
    //  schema.create("PartnerEntity");
    //  final RealmObjectSchema userSchema = schema.create("PartnerEntity");
    //  userSchema.addField("id", Integer.class);
    //  userSchema.addField("title", String.class);
    //  userSchema.addField("partnersPage", String.class);
    //  userSchema.addField("image", String.class);

    ////schema.create("GoodsCategoryEntity");
    //final RealmObjectSchema userSchema1 = schema.create("GoodsCategoryEntity");
    //userSchema1.addField("id", Integer.class);
    //userSchema1.addField("title", String.class);
    //userSchema1.addField("text", String.class);
    //userSchema1.addRealmListField("children", schema.get("GoodsSubCategoryEntity"));

    if(oldVersion == 1)
    {
      if(!schema.contains("ImageModel")) {
        final RealmObjectSchema realmObjectSchema = schema.create("ImageModel");
        //realmObjectSchema.removeField("id");
        realmObjectSchema.addField("id",Integer.class, FieldAttribute.PRIMARY_KEY);
        realmObjectSchema.addRealmListField("imageUrl", String.class);
        realmObjectSchema.addRealmListField("md5", String.class);
        realmObjectSchema.addField("level", String.class);
        realmObjectSchema.addField("duration", Integer.class);
        realmObjectSchema.addField("steps", Integer.class);
      }

      if(!schema.contains("GetAnimationResponse")) {
        final RealmObjectSchema realmObjectSchema1 = schema.create("GetAnimationResponse");
        //realmObjectSchema1.removeField("id");
        realmObjectSchema1.addField("id",Integer.class, FieldAttribute.PRIMARY_KEY);
        realmObjectSchema1.addRealmListField("animations", schema.get("ImageModel"));
      }
    }
  }
}

