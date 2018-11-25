package ps.police.pcore.v2.entity.location

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.util.Holders

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Location requests between model and views.
 *@see LocationService
 *@see FormatService
**/
class LocationController  {

    LocationService locationService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]

    /**
     * used to get location down to top selection
     */
    def getLocationInfo = {
        String entityName = params["entityName"]
        Object serviceObject = Holders.applicationContext.getBean(entityName+"Service")
        Object domainClassInstance = serviceObject."get${entityName?.capitalize()}"(PCPUtils.convertParamsToSearchBean(params))
        if(domainClassInstance){
            JSON.use('deep'){
                render text: ([data:domainClassInstance] as JSON), contentType: "application/json"
            }
        }else {
            render "${message(code: 'default.systemError.label')}"
        }
    }
}

