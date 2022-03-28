package project.tastyfood.service;

import project.tastyfood.model.entity.Picture;
import project.tastyfood.model.service.PictureServiceModel;

public interface PictureService {
    PictureServiceModel addPicture(PictureServiceModel pictureServiceModel);
    void deleteById(String id);
    Picture findByUrl(String pictureUrl);
}
