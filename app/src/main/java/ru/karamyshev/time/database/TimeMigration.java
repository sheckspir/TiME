package ru.karamyshev.time.database;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

class TimeMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();


        if (oldVersion == 0) {
            schema.create("RealmMemoir")
                    .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                    .addField("date", Date.class)
                    .addField("text", String.class)
                    .addField("timeType", int.class);
            oldVersion++;
        }
    }
}
