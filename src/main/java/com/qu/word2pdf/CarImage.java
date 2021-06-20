package com.qu.word2pdf;

import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.template.annotations.FieldMetadata;
import fr.opensagres.xdocreport.template.annotations.ImageMetadata;

public class CarImage {

    private String photoName;

    private IImageProvider photo;

    public CarImage(String photoName, IImageProvider photo) {
        this.photoName = photoName;
        this.photo = photo;
    }


    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    @FieldMetadata( images = { @ImageMetadata( name = "photo" ) } )
    public IImageProvider getPhoto() {
        return photo;
    }

    public void setPhoto(IImageProvider photo) {
        this.photo = photo;
    }
}
