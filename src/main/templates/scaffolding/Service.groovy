<%=packageName ? "package ${packageName}" : ''%>

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.${versionNumber}.DeleteBean
import ps.police.common.utils.${versionNumber}.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 *<h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 *@see MessageSource
 *@see FormatService
 **/
@Transactional
class ${className}Service {

    MessageSource messageSource
    def formatService



    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],<%def fields=domainClass?.persistentProperties;fields.eachWithIndex {field,index->%><% if(field.type.simpleName == "TrackingInfo" ){ %><%} else if(field.type.simpleName == "DescriptionInfo" ){ %>
        [sort: true, search: true, hidden: false, name: "${field.name}.localName", type: "${field.type.simpleName}", source: 'domain'],<%} else if(field.isEnum()){ %>
        [sort: true, search: true, hidden: false, name: "${field.name}", type: "enum", source: 'domain'],<%}else{%>
        [sort: true, search: ${field.type.simpleName in ['String','Integer','Long','Double']}, hidden: false, name: "${field.name}", type: "${field.type.simpleName}", source: 'domain']${index == (fields.size()-1) ? "": ","}<%}%><%}%>
    ]


    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params){
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if(column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        List<String> ids = params.listString('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }


        List<Map<String,String>> orderBy = params.list("orderBy")<%def propertyNameValue;def propertyNameToSearch;def propertyValueToSearch;
        fields.eachWithIndex { property, i ->propertyNameValue = property.name;propertyNameToSearch = property.name;
            if(property.type.simpleName == "DescriptionInfo" ){%>
                String localName = params["descriptionInfo.localName"]
                String latinName = params["descriptionInfo.latinName"]<%}else{
                if(property.manyToOne || property.oneToOne) propertyNameValue = "${property.name}Id";
                if(property.manyToOne || property.oneToOne)propertyNameToSearch = "${property.name}.id";
                if(property.oneToMany) {
                    propertyNameValue = "${property.name}Ids";
                    propertyNameToSearch = "${property.name}.id";
                }
                if (property.isIdentity() || property.manyToOne || property.oneToOne || Number.class.isAssignableFrom(property.type)) {
                    propertyValueToSearch = "params.long(\"${propertyNameToSearch}\")";
                }else if (property.isEnum()) {
                    propertyValueToSearch = "params[\"${propertyNameToSearch}\"] ? ${property.type.name}.valueOf(params[\"${propertyNameToSearch}\"]) : null"
                }else if (property.type == Boolean.class || property.type == boolean) {
                    propertyValueToSearch = "params.boolean(\"${propertyNameToSearch}\")";
                }else if(property.type == Date.class || property.type == java.sql.Date.class || property.type == java.sql.Time.class) {
                    propertyValueToSearch = "params.date(\"${propertyNameToSearch}\",'dd/MM/yyyy')";
                }else if(property.type == java.time.ZonedDateTime.class) {
                    propertyValueToSearch = "PCPUtils.parseZonedDateTime(params[\'${propertyNameToSearch}\'])";
                }else if(property.type == java.util.Set.class || property.type == java.util.List.class) {
                    propertyValueToSearch = "params.listLong(\"${propertyNameToSearch}\")";
                }else if(property.type.simpleName == "TrackingInfo" ){
                }else{propertyValueToSearch =  "params[\"${propertyNameToSearch}\"]"}%><%if(propertyValueToSearch){%><%if(property.type.name.contains("java.") || property.type.name==packageName){%>
                    ${property.type.simpleName} ${propertyNameValue} = ${propertyValueToSearch}<%}else if(property.isEnum()){%>
                    ${property.type.name} ${propertyNameValue} = ${propertyValueToSearch}<%}else{%>
                    Long ${propertyNameValue} = ${propertyValueToSearch}<%}%><%}%><%}%><%propertyNameValue = null;propertyNameToSearch=null;propertyValueToSearch=null}%>

        return ${className}.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{<%fields.eachWithIndex { property, i ->%><%if(property.type.simpleName == "DescriptionInfo"){%>
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)<%}else if(property.type == String.class){%>
                    ilike("${property.name}", sSearch) <%}%><%}%>
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }<%propertyNameValue = null;propertyNameToSearch=null;propertyValueToSearch=null
                fields.eachWithIndex { property, i ->
                    propertyNameValue = property.name
                    propertyNameToSearch = property.name
                    if(property.manyToOne || property.oneToOne) propertyNameValue = "${property.name}Id";
                    if(property.manyToOne || property.oneToOne)propertyNameToSearch = "${property.name}.id";
                    if(property.oneToMany) {
                        propertyNameValue = "${property.name}Ids";
                        propertyNameToSearch = "${property.name}.id";
                    }
                    if (Integer.class.isAssignableFrom(property.type))
                        propertyValueToSearch = "params.int(\"${propertyNameToSearch}\")";
                    if (property.isIdentity() || property.manyToOne || property.oneToOne || Number.class.isAssignableFrom(property.type))
                        propertyValueToSearch = "params.long(\"${propertyNameToSearch}\")";
                    else if (property.isEnum())
                        propertyValueToSearch =  "params[\"${propertyNameToSearch}\"] ? ${property.type.name}.valueOf(params[\"${propertyNameToSearch}\"]) : null"
                    else if (property.type == Boolean.class || property.type == boolean.class)
                        propertyValueToSearch =  "params.boolean(\"${propertyNameToSearch}\")";
                    else if(property.type == Date.class || property.type == java.sql.Date.class || property.type == java.sql.Time.class)
                        propertyValueToSearch =  "params.date(\"${propertyNameToSearch}\",'dd/MM/yyyy')";
                    else if(property.type == java.util.Set.class || property.type == java.util.List.class)
                        propertyValueToSearch =  "params.listLong(\"${propertyNameToSearch}\")";
                    else if(property.type == java.time.ZonedDateTime.class)
                        propertyValueToSearch =  "PCPUtils.parseZonedDateTime(\"${propertyNameToSearch}\")";
                    else if(property.type.simpleName == "TrackingInfo") {
                    }
                    else if(property.type.simpleName == "DescriptionInfo") {
                    }
                    else
                        propertyValueToSearch =  "params[\"${propertyNameToSearch}\"]"
                    if(property.type.simpleName == "DescriptionInfo"){%>
                        if (localName){
                            ilike('localName', "%\$localName%")
                        }
                        if (latinName){
                            ilike('latinName', "%\$latinName%")
                        }<%}else if(property.type.simpleName != "TrackingInfo"){%>
                        if(${propertyNameValue}){<%if(property.type == String.class){%>
                            ilike("${propertyNameToSearch}", "%\${${propertyNameValue}}%")
                        }<%}else if(property.type.simpleName == "ZonedDateTime" ){%>
                            le("${propertyNameToSearch}", ${propertyNameValue})
                        }<%}else if(property.type == java.util.Set.class || property.type == java.util.List.class){%>
                        ${property.name}{
                            inList("id", ${propertyNameValue})
                        }
                    }<%}else{%>
                    eq("${propertyNameToSearch}", ${propertyNameValue})
                }<%}%><%}%><%propertyNameValue = null;propertyNameToSearch=null;propertyValueToSearch=null}%>
}
if(orderBy) {
    orderBy.each { row ->
        order(row.name, row.direction ?: "asc")
    }
}else if(columnName){
    order(columnName, dir)
}else {
    //use as default sort to show the last inserted
    order("trackingInfo.dateCreatedUTC", "desc")
}

}
}

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return ${className}.
 */
${className} save(GrailsParameterMap params) {
    ${className} ${propertyName}Instance


    /**
     * in case: id is encoded
     */
    if (params.encodedId) {
        params.id = HashHelper.decode(params.encodedId)
    }


    if (params.id) {
        ${propertyName}Instance = ${className}.get(params["id"])
        if (params.long("version")) {
            long version = params.long("version")
            if (${propertyName}Instance.version > version) {
                ${propertyName}Instance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('${propertyName}.label', null, '${propertyName}',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this ${propertyName} while you were editing")
                return ${propertyName}Instance
            }
        }
        if (!${propertyName}Instance) {
            ${propertyName}Instance = new ${className}()
            ${propertyName}Instance.errors.reject('default.not.found.message' ,[messageSource.getMessage('${propertyName}.label', null, '${propertyName}',LocaleContextHolder.getLocale())] as Object[], "This ${propertyName} with \${params.id} not found")
            return ${propertyName}Instance
        }
    } else {
        ${propertyName}Instance = new ${className}()
    }
    try {
        ${propertyName}Instance.properties = params;
        ${propertyName}Instance.save(failOnError:true);
    }
    catch (Exception ex) {
        transactionStatus.setRollbackOnly()
        ${propertyName}Instance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
    }
    return ${propertyName}Instance
}

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
    try {
        if (isEncrypted && HashHelper.decodeList(deleteBean.ids)){
            ${className}.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
            deleteBean.status = true
        }else if (deleteBean.ids){
            ${className}.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
            deleteBean.status = true
        }
    }
    catch (Exception ex) {
        deleteBean.status = false
        deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))
    }
    return deleteBean

}

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return ${className}.
 */
@Transactional(readOnly = true)
        ${className} getInstance(GrailsParameterMap params) {
    if (params.encodedId) {
        params.id = HashHelper.decode(params.encodedId)
    }
    //if id is not null then return values from search method
    if (params.id) {
        PagedResultList results = search(params)
        if (results) {
            return results[0]
        }
    }
    return null

}

/**
 * to auto complete model entry.
 * @param GrailsParameterMap params the search map.
 * @return JSON.
 */
@Transactional(readOnly = true)
JSON autoComplete(GrailsParameterMap params) {
    List<Map> dataList = []
    String idProperty = params["idProperty"]?:"id"
    String nameProperty = params["nameProperty"]?:"descriptionInfo.localName"
    List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo")?:[]
    try {
        grails.gorm.PagedResultList resultList = this.search(params)
        dataList = PCPUtils.toMapList(resultList,nameProperty,idProperty,autoCompleteReturnedInfo)
    } catch (Exception ex) {
        ex.printStackTrace()
    }
    return dataList as JSON
}

/**
 * Convert paged result list to map depends on DOMAINS_COLUMNS.
 * @param def resultList may be PagedResultList or PagedList.
 * @param GrailsParameterMap params the search map
 * @param List<String> DOMAIN_COLUMNS the list of model column names.
 * @return Map.
 * @see PagedResultList.
 * @see PagedList.
 */
@Transactional(readOnly = true)
public Map resultListToMap(def resultList,GrailsParameterMap params,List<String> DOMAIN_COLUMNS = null) {
    if(!DOMAIN_COLUMNS) {
        DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
    }
    Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
    formatService.cleanUpGorm();
    return dataToRender
}

}