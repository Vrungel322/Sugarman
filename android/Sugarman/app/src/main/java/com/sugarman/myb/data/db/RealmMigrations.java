package com.sugarman.myb.data.db;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import timber.log.Timber;

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

    Timber.e("old_version "  + oldVersion);

    if (oldVersion == 1) {
      if (!schema.contains("ImageModel")) {
        final RealmObjectSchema realmObjectSchema = schema.create("ImageModel");
        //realmObjectSchema.removeField("id");
        realmObjectSchema.addField("id", Integer.class, FieldAttribute.PRIMARY_KEY);
        realmObjectSchema.addRealmListField("imageUrl", String.class);
        realmObjectSchema.addRealmListField("md5", String.class);
        realmObjectSchema.addField("level", String.class);
        realmObjectSchema.addField("duration", Integer.class);
        realmObjectSchema.addField("steps", Integer.class);
      }

      if (!schema.contains("GetAnimationResponse")) {
        final RealmObjectSchema realmObjectSchema1 = schema.create("GetAnimationResponse");
        //realmObjectSchema1.removeField("id");
        realmObjectSchema1.addField("id", Integer.class, FieldAttribute.PRIMARY_KEY);
        realmObjectSchema1.addRealmListField("animations", schema.get("ImageModel"));
      }
      oldVersion++;
    }

    if (oldVersion == 2) {
      final boolean[] needToChange = { false };
      Timber.e("Entered migration");
      if (schema.contains("ImageModel")) {
        RealmObjectSchema schema1 = schema.get("ImageModel").addField("id_tmp", String.class).transform(obj -> {
          try {
            int oldType = obj.getInt("id");
            obj.setString("id_tmp", Integer.toString(oldType));
          }
          catch (IllegalArgumentException ex)
          {
            Timber.e("Illegal argument!");
            needToChange[0] = true;
          }
        });
        if(needToChange[0])
            schema1.removeField("id").renameField("id_tmp", "id");
        if(!schema.get("ImageModel").getFieldNames().contains("name"))
        schema.get("ImageModel")
            .addField("name", String.class)
            .addField("downloadImmediately", Boolean.class);
      }
      if (!schema.contains("Rule")) {
        final RealmObjectSchema realmObjectSchema = schema.create("Rule");
        realmObjectSchema.addField("id", String.class, FieldAttribute.PRIMARY_KEY);
        realmObjectSchema.addField("action", String.class);
        realmObjectSchema.addField("ruleType", Integer.class);
        realmObjectSchema.addField("count", Integer.class);
        realmObjectSchema.addField("message", String.class);
        realmObjectSchema.addField("name", String.class);
        realmObjectSchema.addField("nameOfAnim", String.class);
        realmObjectSchema.addField("sequence", Integer.class);
        realmObjectSchema.addField("groupCount", Integer.class);
        realmObjectSchema.addField("popUpImg", String.class);
      } else {

        if(!schema.get("Rule").getFieldNames().contains("id"))
        schema.get("Rule")
            .addField("id", String.class, FieldAttribute.PRIMARY_KEY);
        else
        {
          
        }
        if(!schema.get("Rule").getFieldNames().contains("popUpImg"))
          schema.get("Rule").addField("popUpImg", String.class);
      }

      if(!schema.contains("RuleSet"))
      {
        final RealmObjectSchema realmObjectSchema = schema.create("RuleSet");
        realmObjectSchema.addField("id", Integer.class, FieldAttribute.PRIMARY_KEY);
        realmObjectSchema.addRealmListField("rules", schema.get("Rule"));
      }
      oldVersion++;
    }
  }
}

/*
  @PrimaryKey private Integer id;
  @Getter @Setter @SerializedName("rules") private RealmList<Rule> rules;
  */