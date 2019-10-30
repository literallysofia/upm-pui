# upm-pui

## Guidelines


TODO:

[] Toast login
[] Toast edit photo

An article will be composed by
  - **Tittle**
  - Subtitle
  - **Category** 
  - **Abstract** 
  - Body

**Mandatory** fields must be validated by the different forms when the user is filling the form to
add/modify the article

Additionally, the server returns a field update_date with the timestamp of the last modification
of the given article.

Depending the request, the image associated to each article are returned in two different fields:

- *image_data* and *image_media_type* when the request is asking for one concrete news
- *thumbnail_data* and *thumbnail_media_type* when the request is asking for a list of news
- Categories available are: **National**, **Economy**, **Sports** and **Technology**

**Android application** functionalities are limited to show the list of articles and update the picture of
a given article. 


Projects for the Programming of User Interfaces (PUI) class of the Master in Human Computer Interaction and Design of EIT Digital Master School at Universidad Politécnica de Madrid (UPM).

Made in colaboration with [Daniel Machado]() and [Pepi Nedkova]().

**Completed in XX/01/2020.**
