# wljsa module

Web module to manage LJSA jobs.

## Dependencies

**wrapp**

- `git clone https://github.com/giosil/wrapp.git` 
- `mvn clean install` - this will produce `wrapp.war` in `target` directory

## Build and deploy web application with Wrapp

- Create if not exists `$HOME/cfg` directory
- Copy json files from `cfg` to `$HOME/cfg`
- Deploy `wrapp.war` in your application server
- `git clone https://github.com/giosil/LJSA.git` 
- `cd gui-web`
- `mvn clean install` - this will produce `wljsa.war` in `target` directory
- Launch `http://localhost:8080/wrapp` 
- Enter (whatever) credentials on the login page (no check done in dev configuration)

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
