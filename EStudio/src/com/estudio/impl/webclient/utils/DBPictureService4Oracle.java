package com.estudio.impl.webclient.utils;

public final class DBPictureService4Oracle extends DBPictureService {

    @Override
    protected String getSelectSQL() {
        return "select file_name,content_type,pic_width,pic_height, save_filename,thumbnail_filename from sys_attachment_4_picture where picture_type=:type and id=:id";
    }

    @Override
    protected String getDeleteSQL() {
        return "delete from sys_attachment_4_picture where picture_type=:type and id=:id";
    }

    @Override
    protected String getInsertSQL() {
        return new StringBuilder().append("insert into sys_attachment_4_picture\n") //
                .append("  (id, picture_type, file_name,save_filename,thumbnail_filename, content_type, file_size, pic_width, pic_height, content)\n") //
                .append("values\n") //
                .append("  (:id, :picture_type, :file_name,:save_filename,:thumbnail_filename, :content_type, :file_size, :pic_width, :pic_height, :content)").toString();

    }

    private DBPictureService4Oracle() {
        super();
    }

    private static final DBPictureService4Oracle INSTANCE = new DBPictureService4Oracle();

    public static DBPictureService getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getGarbageSQL() {
        return "insert into sys_attachment_garbage (id, key_id, save_filename) values (:id, :key_id, :save_filename)";
    }

}
