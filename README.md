# PopMovies
## Lightweight Movie App that allows users to discover top and popular movies playing.


*Based on Udacity course assignment.*

- Upon launch, present the user with an grid arrangement of movie posters.
- Allow your user to change sort order via a setting:
  - The sort order can be by most popular, or by top rated
- Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
  - original title
  - movie poster image thumbnail
  - A plot synopsis 
  - user rating 
  - release date
  - Ability to watch trailer


### Mock:
<img src="https://github.com/IvanLepi/PopularMovies/blob/master/screenshots/Phone_detail_with_settings_mockup.png?raw=true" width="300" height="533" /> <img src="https://github.com/IvanLepi/PopularMovies/blob/master/screenshots/Phone_main_mockup.png?raw=true" width="300" height="533"/>



### End Result:
<img src="https://github.com/IvanLepi/PopularMovies/blob/master/screenshots/details.gif?raw=true"/>  <img src="https://github.com/IvanLepi/PopularMovies/blob/master/screenshots/scroll.gif?raw=true"/>  <img src="https://github.com/IvanLepi/PopularMovies/blob/master/screenshots/sort.gif?raw=true"/>  

### Libraries used:
[RxJava2][1], [RxAndroid][3], [Retrofit2][2], [Retrolambda][4], [Picasso][5].

### TODO:
- [ ] Allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request*.
- [ ] Modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.
- [ ] Document


[1]: https://github.com/ReactiveX/RxJava
[2]: https://github.com/square/retrofit	
[3]: https://github.com/ReactiveX/RxAndroid
[4]: https://github.com/orfjackal/retrolambda
[5]: https://github.com/square/picasso
