package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.lookups.ContactMethodService
import ps.police.pcore.v2.entity.lookups.ContactTypeService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.dtos.v1.ContactTypeDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * this service is aims to create department contact info for department
 * <h1>Usage</h1>
 * -used for department
 * <h1>Restriction</h1>
 * - needs  department and firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DepartmentContactInfoService {

    MessageSource messageSource
    def formatService
    ContactTypeService contactTypeService
    ContactMethodService contactMethodService
    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.contactTypeName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.contactMethodName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "value", type: "String", source: 'domain']

    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.contactTypeName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.contactMethodName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "value", type: "String", source: 'domain']

    ]
    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        // global setting.

        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName

        if (column) {
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


        List<Map<String, String>> orderBy = params.list("orderBy")
        Long contactMethodId = params.long("contactMethodId")
        Long contactTypeId = params.long("contactTypeId")
        String departmentId = params['department.id']
        String value = params["value"]
        String status = params["status"]

        PagedResultList departmentContactInfoList = DepartmentContactInfo.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("value", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (contactMethodId) {
                    eq("contactMethodId", contactMethodId)
                }
                if (contactTypeId) {
                    eq("contactTypeId", contactTypeId)
                }
                if (departmentId) {
                    eq("department.id", departmentId)
                }
                if (value) {
                    ilike("value", "%${value}%")
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case 'transientData.contactTypeName':
                        order("contactTypeId", dir)
                        break;
                    case 'transientData.contactMethodName':
                        order("contactMethodId", dir)
                        break;
                    default:
                        order(columnName, dir)
                }
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }
        }
        return departmentContactInfoList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return DepartmentContactInfo.
     */
    DepartmentContactInfo save(GrailsParameterMap params) {
        DepartmentContactInfo departmentContactInfoInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            departmentContactInfoInstance = DepartmentContactInfo.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (departmentContactInfoInstance.version > version) {
                    departmentContactInfoInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('departmentContactInfo.label', null, 'departmentContactInfo', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this departmentContactInfo while you were editing")
                    return departmentContactInfoInstance
                }
            }
            if (!departmentContactInfoInstance) {
                departmentContactInfoInstance = new DepartmentContactInfo()
                departmentContactInfoInstance.errors.reject('default.not.found.message', [messageSource.getMessage('departmentContactInfo.label', null, 'departmentContactInfo', LocaleContextHolder.getLocale())] as Object[], "This departmentContactInfo with ${params.id} not found")
                return departmentContactInfoInstance
            }
        } else {
            departmentContactInfoInstance = new DepartmentContactInfo()
        }
        try {
            departmentContactInfoInstance.properties = params
            departmentContactInfoInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            departmentContactInfoInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return departmentContactInfoInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {

            def id
            //if the id is encrypted
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = HashHelper.decode(deleteBean?.ids[0])
            } else {
                id = deleteBean?.ids[0]
            }

            DepartmentContactInfo instance = DepartmentContactInfo.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('department.deleteMessage.label')
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return DepartmentContactInfo.
     */
    @Transactional(readOnly = true)
    DepartmentContactInfo getInstance(GrailsParameterMap params) {
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

    @Transactional(readOnly = true)
    DepartmentContactInfo getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
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
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "value"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List < String >  DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     * @see PagedResultList.
     * @see PagedList.
     */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }


    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList departmentContactInfoList = search(params)
        if (departmentContactInfoList) {
            List<ContactTypeDTO> contactTypeList
            List<ContactTypeDTO> contactMethodList
            SearchBean searchBean
            //get contact type's list by ids
            if (departmentContactInfoList?.resultList?.contactTypeId) {
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: departmentContactInfoList?.resultList?.contactTypeId))
                contactTypeList = contactTypeService?.searchContactType(searchBean)?.resultList

            }

            //get contact method's list by ids
            if (departmentContactInfoList?.resultList?.contactMethodId) {
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: departmentContactInfoList?.resultList?.contactMethodId))
                contactMethodList = contactMethodService?.searchContactMethod(searchBean)?.resultList

            }

            departmentContactInfoList.each { DepartmentContactInfo departmentContactInfo ->

                departmentContactInfo.transientData = [:]
                departmentContactInfo.transientData.put("contactTypeName", contactTypeList?.find {
                    it?.id == departmentContactInfo?.contactTypeId
                }?.descriptionInfo?.localName)

                departmentContactInfo.transientData.put("contactMethodName", contactMethodList?.find {
                    it?.id == departmentContactInfo?.contactMethodId
                }?.descriptionInfo?.localName)
            }
        }
        return departmentContactInfoList
    }
}