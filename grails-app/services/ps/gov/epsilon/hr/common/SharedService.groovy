package ps.gov.epsilon.hr.common

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.core.GrailsClass
import grails.core.GrailsDomainClassProperty
import grails.transaction.Transactional
import grails.util.Environment
import grails.util.Holders
import grails.util.TypeConvertingMap
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.sql.Sql
import guiplugin.FormatService
import org.grails.core.DefaultGrailsDomainClass
import org.springframework.context.MessageSource
import ps.epsilon.attach.AttachmentFile
import ps.epsilon.attach.AttachmentManageService
import ps.epsilon.attach.AttachmentService
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.enums.v1.EnumDepartmentType
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.enums.workflow.v1.EnumWorkFlowOperation
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceType
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.employmentService.lookups.ServiceActionReason
import ps.gov.epsilon.hr.firm.employmentService.lookups.ServiceActionReasonType
import ps.gov.epsilon.hr.firm.lookups.DepartmentType
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.lookups.JobCategory
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.settings.FirmDocument
import ps.gov.epsilon.hr.firm.settings.FirmSetting
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.gov.epsilon.workflow.OperationWorkflowSetting
import ps.gov.epsilon.workflow.OperationWorkflowSettingParam
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.MenuLevel
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.efsms.node.beans.NodeDataBean
import ps.police.navigation.NavigationItem
import ps.police.notifications.NotificationType
import ps.police.pcore.enums.v1.RelationshipTypeEnum
import ps.police.security.JoinedRoleGroup
import ps.police.security.Permission
import ps.police.security.Requestmap
import ps.police.security.SecurityPluginBootStrapService
import ps.police.security.beans.GroupBean
import ps.police.security.beans.PermissionBean
import ps.police.security.commands.v1.RoleCommand
import ps.police.security.dtos.v1.RoleDTO
import ps.police.security.remotting.RemoteRoleService
import ps.police.serviceCatalog.commands.v1.ProviderCommand

import java.time.ZonedDateTime

@Transactional
class SharedService {

    SecurityPluginBootStrapService securityPluginBootStrapService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    AttachmentService attachmentService
    AttachmentManageService attachmentManageService
    GrailsApplication grailsApplication
    RemoteRoleService remoteRoleService
    def dataSource
    FormatService formatService
    Locale arabicLocal

    void initSetup() {
        TypeConvertingMap.metaClass.listLong { str ->
            str = delegate[str];
            if (!str)
                return []
            if (str instanceof List)
                return str.collect { it.toLong() }
            return Arrays.asList(str.toString()?.replace("[", "")?.replace("]", "")?.split("\\s*,\\s*")).collect {
                it?.trim()?.toLong()
            };
        }

        TypeConvertingMap.metaClass.listString { str ->
            str = delegate[str];
            if (!str)
                return []
            if (str instanceof List)
                return str.collect { it }
            return Arrays.asList(str.toString()?.replace("[", "")?.replace("]", "")?.split("\\s*,\\s*")).collect {
                it?.trim()
            };
        }

        JSON.registerObjectMarshaller(Department) { Department department ->
            Map map = [:]
            def domain = new DefaultGrailsDomainClass(Department.class)
            map.put("id", department?.id)
            map.put("version", department?.version)
            map.put("encodedId", department?.encodedId)
            map.put("transientData", department?.transientData)
            domain.persistentProperties.each { GrailsDomainClassProperty property ->
                map.put(property.name, department?."${property.name}")
            }
            return map
        }

        arabicLocal = new Locale("ar")
    }

    void createFirms() {

        def dataList = [
                [code: "DCO", name: 'الارتباط العسكري', coreOrganizationId: 1L],
                [code: "PCP", name: 'الشرطة', coreOrganizationId: 1L],
                [code: "CD", name: 'الدفاع المدني', coreOrganizationId: 1L],
                [code: "NSF", name: 'الامن الوطني', coreOrganizationId: 1L],
                [code: "PG", name: 'حرس الرئيس', coreOrganizationId: 1L],
                [code: "AOC", name: 'هيئة التنظيم والادارة', coreOrganizationId: 1L],
        ]

        Firm dataInstance
        dataList?.each { record ->
            dataInstance = Firm.createCriteria().get {
                eq('code', record?.code)
            }
            if (!dataInstance) {
                dataInstance = new Firm(record).save(failOnError: true, flush: true)
                println "Fresh Database. Creating ${dataInstance?.name} firm."
            }
        }
    }

    void createDepartmentTypes() {
        def dataList = [
                [localName: "محافظة", latinName: 'Governerote', staticDepartmentType: EnumDepartmentType.GOVERNEROTE],
                [localName: "ادارة", latinName: 'Division', staticDepartmentType: EnumDepartmentType.DIVISION],
                [localName: "دائرة", latinName: 'Department', staticDepartmentType: EnumDepartmentType.DEPARTMENT],
                [localName: "قسم", latinName: 'Section', staticDepartmentType: EnumDepartmentType.SECTION],
                [localName: "وحدة", latinName: 'Unit', staticDepartmentType: EnumDepartmentType.UNIT],
                [localName: "مركز", latinName: 'Station', staticDepartmentType: EnumDepartmentType.STATION],
                [localName: "شعية", latinName: 'Branch', staticDepartmentType: EnumDepartmentType.BRANCH]
        ]
        DepartmentType dataInstance
        DescriptionInfo descriptionInfo
        dataList?.each { record ->
            descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record?.latinName)
            Firm.list()?.each { firmRecord ->
                dataInstance = DepartmentType.createCriteria().get {
                    eq('localName', descriptionInfo?.localName)
                    eq('firm.id', firmRecord?.id)
                }
                if (!dataInstance) {
                    dataInstance = new DepartmentType(descriptionInfo: descriptionInfo, firm: firmRecord, staticDepartmentType: record.staticDepartmentType).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${dataInstance?.descriptionInfo} Military Rank for firm ${firmRecord?.name}."
                }
            }
        }
    }

    void createDepartments() {
        def dataList = [
                [localName: "الادارة العامة", latinName: 'General Department', enumDepartmentType: EnumDepartmentType.GOVERNEROTE, governorateId: 7L, firmCode: "PCP"],
                [localName: "الادارة العامة", latinName: 'General Department', enumDepartmentType: EnumDepartmentType.GOVERNEROTE, governorateId: 7L, firmCode: "DCO"],
                [localName: "الادارة العامة", latinName: 'General Department', enumDepartmentType: EnumDepartmentType.GOVERNEROTE, governorateId: 7L, firmCode: "CD"],
                [localName: "الادارة العامة", latinName: 'General Department', enumDepartmentType: EnumDepartmentType.GOVERNEROTE, governorateId: 7L, firmCode: "NSF"],
                [localName: "الادارة العامة", latinName: 'General Department', enumDepartmentType: EnumDepartmentType.GOVERNEROTE, governorateId: 7L, firmCode: "PG"],
                [localName: "الادارة العامة", latinName: 'General Department', enumDepartmentType: EnumDepartmentType.GOVERNEROTE, governorateId: 7L, firmCode: "AOC"]
        ]

        Department dataInstance
        DescriptionInfo descriptionInfo
        Firm firm
        DepartmentType departmentType
        dataList?.each { record ->
            descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record.latinName)
            firm = Firm.findByCode(record?.firmCode)
            departmentType = DepartmentType.createCriteria().get {
                eq('staticDepartmentType', record.enumDepartmentType)
                eq('firm.id', firm?.id)
            }
            dataInstance = Department.createCriteria().get {
                eq('latinName', descriptionInfo?.latinName)
                eq('firm.id', firm?.id)
            }
            if (!dataInstance && departmentType != null) {
                dataInstance = new Department(record)
                dataInstance.descriptionInfo = descriptionInfo
                dataInstance.firm = firm
                dataInstance.departmentType = departmentType
                dataInstance.save(failOnError: true, flush: true)
                println "Fresh Database. Creating ${dataInstance?.descriptionInfo} department for firm ${dataInstance?.firm}."
            }
        }
    }

    void createEmployees() {

        ZonedDateTime date = PCPUtils.parseZonedDateTime("01/01/2017")
        ZonedDateTime nullDate = PCPUtils.DEFAULT_ZONED_DATE_TIME

        def dataList = [
                [personId: 7992L, financialNumber: "0000003", firmCode: "PCP", employmentNumber: "0000003", employmentDate: date, joinDate: date, categoryStatusDate: date, orderDate: date, attendanceStatusDate: date],
                [personId: 1124726L, financialNumber: "0000004", firmCode: "DCO", employmentNumber: "0000004", employmentDate: date, joinDate: date, categoryStatusDate: date, orderDate: date, attendanceStatusDate: date],
                [personId: 2673687L, financialNumber: "0000005", firmCode: "CD", employmentNumber: "0000005", employmentDate: date, joinDate: date, categoryStatusDate: date, orderDate: date, attendanceStatusDate: date],
                [personId: 1197930L, financialNumber: "0000006", firmCode: "NSF", employmentNumber: "0000006", employmentDate: date, joinDate: date, categoryStatusDate: date, orderDate: date, attendanceStatusDate: date],
                [personId: 37169L, financialNumber: "0000007", firmCode: "PG", employmentNumber: "0000007", employmentDate: date, joinDate: date, categoryStatusDate: date, orderDate: date, attendanceStatusDate: date],
                [personId: 2148647L, financialNumber: "0000008", firmCode: "AOC", employmentNumber: "0000008", employmentDate: date, joinDate: date, categoryStatusDate: date, orderDate: date, attendanceStatusDate: date],
        ]


        Map currentEmploymentRecord = [fromDate: date, toDate: nullDate, internalOrderDate: nullDate]
        Map currentEmployeeMilitaryRank = [actualDueDate: date, dueDate: date, orderDate: date, managerialRankDate: nullDate, militaryRankTypeDate: nullDate, dueReason: EnumPromotionReason.ELIGIBLE, managerialOrderNumber: "SETUP"]

        Employee dataInstance
        EmploymentRecord employmentRecordInstance
        EmployeePromotion employeePromotionInstance
        Department departmentInstance
        MilitaryRank militaryRankInstance
        Firm firm
        EmployeeStatusCategory employeeStatusCategory
        EmploymentCategory employmentCategory
        EmployeeStatusHistory employeeStatusHistory
        EmployeeStatus employeeStatus

        dataList?.each { record ->
            dataInstance = Employee.createCriteria().get {
                eq('personId', record?.personId)
            }
            if (!dataInstance) {
                dataInstance = new Employee(record)
                employmentRecordInstance = new EmploymentRecord(currentEmploymentRecord)
                employeePromotionInstance = new EmployeePromotion(currentEmployeeMilitaryRank)
                firm = Firm.findByCode(record?.firmCode)

                //set militaryRank
                militaryRankInstance = MilitaryRank.createCriteria().get {
                    eq('latinName', "Lieutenant Colonel")
                    eq('firm.id', firm?.id)
                }

                //set department
                departmentInstance = Department.createCriteria().get {
                    eq('latinName', "General Department")
                    eq('firm.id', firm?.id)
                }

                //get employeeStatusCategory
                employeeStatusCategory = EmployeeStatusCategory.createCriteria().get {
                    eq('latinName', "Committed")
                    eq('firm.id', firm?.id)
                }

                //get employmentCategory
                employmentCategory = EmploymentCategory.createCriteria().get {
                    eq('latinName', "Soldier")
                    eq('firm.id', firm?.id)
                }

                //get employeeStatus
                employeeStatus = EmployeeStatus.createCriteria().get {
                    eq('latinName', "Working")
                    eq('firm.id', firm?.id)
                    eq('employeeStatusCategory', employeeStatusCategory)
                }
                //fill status
                employeeStatusHistory = new EmployeeStatusHistory(fromDate: date, toDate: nullDate, employeeStatus: employeeStatus)
                employeeStatusHistory.transientData.firm = firm
                employmentRecordInstance.department = departmentInstance
                employmentRecordInstance.employmentCategory = employmentCategory
                employmentRecordInstance.firm = firm
                employeePromotionInstance.militaryRank = militaryRankInstance
                employeePromotionInstance.firm = firm
                dataInstance.firm = firm
                dataInstance.categoryStatus = employeeStatusCategory
                dataInstance.addToEmployeeMilitaryRank(employeePromotionInstance)
                dataInstance.addToEploymentRecord(employmentRecordInstance)

                //save
                dataInstance.save(failOnError: true, flush: true)
                dataInstance.currentEmploymentRecord = employmentRecordInstance
                dataInstance.currentEmployeeMilitaryRank = employeePromotionInstance
                dataInstance.addToEmployeeStatusHistories(employeeStatusHistory)
                dataInstance.save(failOnError: true, flush: true)
                println "Fresh Database. Creating ${dataInstance?.financialNumber} employee for firm ${dataInstance?.firm}."
            }
        }
    }

    void createBuiltInRoles() {

        Boolean developmentModeWithServiceCatalog = Holders.grailsApplication.config.grails.developmentModeWithServiceCatalog

        if (Environment.current == Environment.PRODUCTION || developmentModeWithServiceCatalog) {

            String applicationName = Holders.grailsApplication.config.grails.applicationName
            String applicationKey = Holders.grailsApplication.config.grails.applicationKey
            List<Map> rolesList = []
            EnumApplicationRole.values().each { EnumApplicationRole applicationRole ->
                rolesList << [authority: applicationRole.getAuthority(), description: applicationRole.getDescription()]
            }
            RoleCommand roleCommand
            def sql
            int numOfRows
            rolesList.each { roleMap ->
                if (!JoinedRoleGroup.findByRoleAuthority(roleMap.authority)) {
                    roleCommand = new RoleCommand()
                    ProviderCommand providerCommand = new ProviderCommand()
                    providerCommand.providerKey = applicationKey
                    providerCommand.providerName = applicationName
                    roleCommand.authority = roleMap.authority?.toString()
                    roleCommand.description = roleMap.description?.toString()
                    roleCommand.isAssignable = true
                    roleCommand.isEditable = true
                    roleCommand.isActive = true
                    roleCommand.provider = providerCommand
                    roleCommand.trackingInfo = securityPluginBootStrapService.getDummyTrackingInfoCommand()
//
                    SearchBean searchBean = new SearchBean();
                    searchBean.searchCriteria.put("authority", new SearchConditionCriteriaBean(operand: "authority", value1: roleMap.authority?.toString()))
                    RoleDTO roleDTO = remoteRoleService.getRole(searchBean)
                    if (!roleDTO && roleCommand.validate() && !roleCommand.hasErrors()) {
                        if (!remoteRoleService.saveRole(roleCommand)?.hasErrors()) {
                            println "Fresh Database. Creating ROLE [${roleCommand.authority}]"
                            if (roleCommand.authority == EnumApplicationRole.ROLE_HR_DEPARTMENT.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority) " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_category not like '%lookups%' " +
                                            "   and group_category not like '%.aoc.%' " +
                                            "   and group_category not like '%hrManagerGroup%' " +
                                            "   and group_category not like '%.aoc.%' " +
                                            "   and group_name <> 'GROUP_SECURITY' " +
                                            "   and group_name not in ('GROUP_FIRM_LIST','GROUP_FIRM_SHOW','GROUP_FIRM_UPDATE','GROUP_FIRM_CREATE','GROUP_FIRM_DELETE')   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }
                            if (roleCommand.authority == EnumApplicationRole.ROLE_LOOKUPS_ADMIN.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority) " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_category  like '%lookup%' " +
                                            " or group_name like 'GROUP_FIRM_%'   " +
                                            " or group_name like 'GROUP_DEPARTMENT_%'   " +
                                            " or group_name like 'GROUP_FIRM_SETTING_%'   " +
                                            " or group_name like 'GROUP_FIRM_DOCUMENT_%'   " +
                                            " or group_name like 'GROUP_FIRM_JOINED_OPERATION_DOCUMENT_%'   " +
                                            " or group_name like 'GROUP_VACATION_CONFIGURATION_%'   " +
                                            " or group_name like 'GROUP_WORKFLOW_%'   " +
                                            "   and group_category not like '%hrManagerGroup%' " +
                                            "   and   group_name <> 'GROUP_SECURITY' " +
//                                            "   and group_name not in ('GROUP_FIRM_LIST','GROUP_FIRM_SHOW','GROUP_FIRM_UPDATE','GROUP_FIRM_CREATE','GROUP_FIRM_DELETE')   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_SUPER_ADMIN.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority) " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_category  like '%hrManagerGroup%' " +
                                            "   and group_name not in ('GROUP_FIRM_LIST','GROUP_FIRM_SHOW','GROUP_FIRM_UPDATE','GROUP_FIRM_CREATE','GROUP_FIRM_DELETE')   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_SYSTEM_ADMIN.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority) " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ('GROUP_FIRM_LIST','GROUP_FIRM_SHOW','GROUP_FIRM_UPDATE','GROUP_FIRM_CREATE','GROUP_FIRM_DELETE') " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AUDIT.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority) " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_category  like '%ps.police.audit%' " +
                                            "   and group_name not in ('GROUP_FIRM_LIST','GROUP_FIRM_SHOW','GROUP_FIRM_UPDATE','GROUP_FIRM_CREATE','GROUP_FIRM_DELETE')   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ('GROUP_FIRM_AUTOCOMPLETE'," +
                                            " 'GROUP_EMPLOYEE_LIST', " +
                                            " 'GROUP_EXTERNAL_TRANSFER_LIST', " +
                                            " 'GROUP_LOAN_REQUEST_LIST', " +
                                            " 'GROUP_DISPATCH_REQUEST_LIST', " +
                                            " 'GROUP_SUSPENSION_REQUEST_LIST', " +
                                            " 'GROUP_APPLICANT_LIST', " +
                                            " 'GROUP_VACATION_REQUEST_LIST', " +
                                            " 'GROUP_UPDATE_MILITARY_RANK_REQUEST_LIST', " +
                                            " 'GROUP_PROMOTION_REQUEST_LIST', " +
                                            " 'GROUP_ALLOWANCE_REQUEST_LIST', " +
                                            " 'GROUP_CHILD_REQUEST_LIST', " +
                                            " 'GROUP_MARITAL_STATUS_REQUEST_LIST', " +
                                            " 'GROUP_EMPLOYEE_VIOLATION_LIST', " +
                                            " 'GROUP_DISCIPLINARY_REQUEST_LIST', " +
                                            " 'GROUP_PETITION_REQUEST_LIST', " +
                                            " 'GROUP_ABSENCE_LIST', " +
                                            " 'GROUP_EMPLOYEE_SALARY_INFO_LIST', " +
                                            " 'GROUP_RETURN_FROM_ABSENCE_REQUEST_LIST', " +
                                            " 'GROUP_EMPLOYMENT_SERVICE_REQUEST_LIST', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_LIST', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_LIST_WORKFLOW', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_SHOW', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_CREATE_INCOMING', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_CREATE_OUTGOING', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_EDIT', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_UPDATE', " +
                                            " 'GROUP_AOC_CORRESPONDENCE_LIST_MANAGE_LIST', " +
                                            " 'GROUP_COMMITTEE_LIST', " +
                                            " 'GROUP_COMMITTEE_CREATE', " +
                                            " 'GROUP_COMMITTEE_UPDATE', " +
                                            " 'GROUP_COMMITTEE_DELETE', " +
                                            " 'GROUP_COMMITTEE_SHOW', " +
                                            " 'GROUP_PROVINCE_LIST', " +
                                            " 'GROUP_PROVINCE_CREATE', " +
                                            " 'GROUP_PROVINCE_UPDATE', " +
                                            " 'GROUP_PROVINCE_DELETE', " +
                                            " 'GROUP_PROVINCE_SHOW', " +
                                            " 'GROUP_PROFILE_NOTICE_CATEGORY_LIST', " +
                                            " 'GROUP_PROFILE_NOTICE_CATEGORY_CREATE', " +
                                            " 'GROUP_PROFILE_NOTICE_CATEGORY_UPDATE', " +
                                            " 'GROUP_PROFILE_NOTICE_CATEGORY_DELETE', " +
                                            " 'GROUP_PROFILE_NOTICE_UPDATE', " +
                                            " 'GROUP_PROFILE_NOTICE_SHOW', " +
                                            " 'GROUP_EMPLOYEE_SAVE_EMPLOYEE_PROFILE_STATUS', " +
                                            " 'GROUP_PROFILE_NOTICE_CATEGORY_SHOW' " +

                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }

                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_EVALUATION_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_EVALUATION_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_EVALUATION_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_EMPLOYEE_EVALUATION_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_RETURN_FROM_ABSENCE_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_RETURN_FROM_ABSENCE_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_RETURN_FROM_ABSENCE_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_RETURN_FROM_ABSENCE_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_VACATION_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_VACATION_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_VACATION_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_VACATION_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_LOAN_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_LOAN_LIST_LIST_INCOMING', " +
                                            " 'GROUP_LOAN_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_LOAN_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_LOAN_NOTICE_REPLAY_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_LOAN_NOTICE_REPLAY_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_SUSPENSION_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_SUSPENSION_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_SUSPENSION_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_SUSPENSION_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_ALLOWANCE_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_ALLOWANCE_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_ALLOWANCE_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_ALLOWANCE_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }
                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_PROMOTION_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_PROMOTION_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_PROMOTION_LIST_LIST_OUTGOING' ," +
                                            " 'GROUP_PROMOTION_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }


                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_CHILD_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_CHILD_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_CHILD_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_CHILD_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_VIOLATION_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_VIOLATION_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_VIOLATION_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_EMPLOYEE_VIOLATION_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_MARITAL_STATUS_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_MARITAL_STATUS_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_MARITAL_STATUS_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_MARITAL_STATUS_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }



                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_EXTERNAL_TRANSFER_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_EXTERNAL_TRANSFER_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_EXTERNAL_TRANSFER_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_EXTERNAL_TRANSFER_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }


                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_DISCIPLINARY_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_DISCIPLINARY_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_DISCIPLINARY_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_DISCIPLINARY_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }


                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_DISPATCH_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_DISPATCH_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_DISPATCH_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_DISPATCH_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_RETURN_TO_SERVICE_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_RETURN_TO_SERVICE_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_RETURN_TO_SERVICE_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_EMPLOYMENT_SERVICE_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }

                            if (roleCommand.authority == EnumApplicationRole.ROLE_AOC_END_OF_SERVICE_LIST.value) {
                                try {
                                    sql = new Sql(dataSource)
                                    numOfRows = sql.executeUpdate("INSERT INTO joined_role_group( " +
                                            "           sec_group_id, role_authority)  " +
                                            "   select id ,'${roleCommand.authority}'  " +
                                            "   from sec_group  " +
                                            "   where group_name in ( " +
                                            " 'GROUP_AOC_END_OF_SERVICE_LIST_LIST_INCOMING', " +
                                            " 'GROUP_AOC_END_OF_SERVICE_LIST_LIST_OUTGOING', " +
                                            " 'GROUP_EMPLOYMENT_SERVICE_REQUEST_CREATE' " +
                                            ")   " +
                                            "   and is_active = true  " +
                                            "   and is_assignable = true ");
                                } catch (Exception e) {
                                    e.printStackTrace()
                                } finally {
                                    if (sql) {
                                        sql.close()
                                    }
                                    println "Fresh Database. Assigend ${numOfRows} groups to ROLE [${roleCommand.authority}]"
                                }
                            }


                        }
                    }
                }
            }
        }
    }

    void createCustomGroupsAndPermissions() {

        //custom for notification
        Requestmap.findByUrl("/stomp") ?: new Requestmap(url: "/stomp", configAttribute: "permitAll").save()
        Requestmap.findByUrl("/stomp/**") ?: new Requestmap(url: "/stomp/**", configAttribute: "permitAll").save()

        Boolean developmentModeWithServiceCatalog = Holders.grailsApplication.config.grails.developmentModeWithServiceCatalog

        if (Environment.current == Environment.PRODUCTION || developmentModeWithServiceCatalog) {

            println "... Saving Custom Groups and Permissions"

            List<Map> permissionGroups = [

                    //notification
                    [groupName       : "GROUP_NOTIFICATION_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('notification.entities', null, 'Notifications', arabicLocal),
                     groupCagtegory  : "notifications",
                     permissionNames : [
                             "ROLE_PERM_NOTIFICATION_IGNORE",
                             "ROLE_PERM_NOTIFICATION_REDIRECT_TO_ACTION",
                             "ROLE_PERM_NOTIFICATION_GET_UN_READ_NOTIFICATIONS",
                             "ROLE_PERM_NOTIFICATION_AUTOCOMPLETE_NOTIFICATION_TYPE",
                     ]
                    ],


                    [groupName       : "GROUP_AOC_CORRESPONDENCE_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocCorrespondenceList.entities', null, 'aocCorrespondenceList outgoing', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences",
                     permissionNames : [
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_OUTGOING",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_FILTER",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_SHOW",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_EDIT",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_DELETE",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_MANAGE_LIST",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_INCOMING",
                     ]
                    ],
                    [groupName       : "GROUP_AOC_CORRESPONDENCE_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocCorrespondenceList.entities', null, 'aocCorrespondenceList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences",
                     permissionNames : [
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_OUTGOING",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_FILTER",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_SHOW",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_EDIT",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_DELETE",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_MANAGE_LIST",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_INCOMING",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_LIST_OUTGOING",
                     ]
                    ],


                    [groupName       : "GROUP_AOC_CORRESPONDENCE_LIST_LIST_WORKFLOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocCorrespondenceList.entities', null, 'aocCorrespondenceList workflow', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences",
                     permissionNames : [
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_LIST_WORKFLOW",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_FILTER_WORKFLOW",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_MANAGE_LIST_WORKFLOW",
                     ]
                    ],
                    [groupName       : "GROUP_AOC_CORRESPONDENCE_LIST_CREATE_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocCorrespondenceList.entities', null, 'aocCorrespondenceList createIncoming', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_COMMITTEE_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_ADD_COPY_TO_MODAL",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_SAVE",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_MANAGE_LIST",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_INCOMING"


                     ]
                    ],

                    [groupName       : "GROUP_AOC_CORRESPONDENCE_LIST_CREATE_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocCorrespondenceList.entities', null, 'aocCorrespondenceList createOutgoing', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_COMMITTEE_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_ADD_COPY_TO_MODAL",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_SAVE",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_MANAGE_LIST",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_OUTGOING"
                     ]
                    ],

                    [groupName       : "GROUP_AOC_CORRESPONDENCE_LIST_MANAGE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocCorrespondenceList.entities', null, 'aocCorrespondenceList manageList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences",
                     permissionNames : [
                             "ROLE_PERM_AOC_LIST_RECORD_FILTER",
                             "ROLE_PERM_AOC_LIST_RECORD_FILTER_NOT_INCLUDED_RECORDS",
                             "ROLE_PERM_AOC_LIST_RECORD_SAVE_EXISTING_RECORDS",
                             "ROLE_PERM_AOC_LIST_RECORD_SAVE",
                             "ROLE_PERM_AOC_LIST_RECORD_DELETE",
                             "ROLE_PERM_AOC_LIST_RECORD_SELECT_EMPLOYEE",
                             "ROLE_PERM_AOC_LIST_RECORD_OPERATIONS_FORM",
                             "ROLE_PERM_AOC_LIST_RECORD_NOTE_LIST",
                             "ROLE_PERM_AOC_LIST_RECORD_NOTE_FILTER",
                             "ROLE_PERM_AOC_LIST_RECORD_NOTE_DELETE",
                             "ROLE_PERM_AOC_LIST_RECORD_NOTE_SAVE",
                             "ROLE_PERM_AOC_LIST_RECORD_NOTE_CREATE_MODAL",
                             "ROLE_PERM_AOC_LIST_RECORD_CHANGE_RECORD_STATUS_MODAL",
                             "ROLE_PERM_AOC_LIST_RECORD_SAVE_STATUS_CHANGE",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_EDIT",
                             "ROLE_PERM_AOC_LIST_RECORD_ADD_REQUEST_MODAL",
                             "ROLE_PERM_AOC_LIST_RECORD_CREATE_REQUEST_MODAL",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_START_WORKFLOW",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_SEND_LIST",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_FINISH_LIST",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_OUTGOING",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_INCOMING",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_LIST_OUTGOING",
                             "ROLE_PERM_AOC_CORRESPONDENCE_LIST_LIST_INCOMING",
                             "ROLE_PERM_WORKFLOW_PATH_DETAILS_UPDATE",
                             "ROLE_PERM_AOC_EVALUATION_LIST"
                     ]
                    ],

                    [groupName       : "GROUP_AOC_EVALUATION_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocEvaluationList.entities', null, 'aocEvaluationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.evaluation",
                     permissionNames : [
                             "ROLE_PERM_AOC_EVALUATION_LIST_LIST_INCOMING",
                             "ROLE_PERM_AOC_EVALUATION_LIST_IMPORT_EVALUATION_DATA",
                             "ROLE_PERM_AOC_EVALUATION_LIST_IMPORT_EVALUATION_DATA_MODAL"
                     ]
                    ],

                    [groupName       : "GROUP_AOC_EVALUATION_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocEvaluationList.entities', null, 'aocEvaluationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.evaluation",
                     permissionNames : [
                             "ROLE_PERM_AOC_EVALUATION_LIST_LIST_OUTGOING"
                     ]
                    ],


                    [groupName       : "GROUP_AOC_RETURN_FROM_ABSENCE_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceList.entities', null, 'aocEvaluationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.absence",
                     permissionNames : [
                             "ROLE_PERM_AOC_RETURN_FROM_ABSENCE_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_RETURN_FROM_ABSENCE_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceList.entities', null, 'aocEvaluationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.absence",
                     permissionNames : [
                             "ROLE_PERM_AOC_RETURN_FROM_ABSENCE_LIST_LIST_OUTGOING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_VACATION_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocVacationList.entities', null, 'aocVacationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.vacation",
                     permissionNames : [
                             "ROLE_PERM_AOC_VACATION_LIST_LIST_INCOMING",
                             "ROLE_PERM_VACATION_REQUEST_FILTER_CAN_HAVE_OPERATION",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_VACATION_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocVacationList.entities', null, 'aocVacationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.vacation",
                     permissionNames : [
                             "ROLE_PERM_AOC_VACATION_LIST_LIST_OUTGOING",
                             "ROLE_PERM_VACATION_REQUEST_FILTER_CAN_HAVE_OPERATION",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_LOAN_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocLoanList.entities', null, 'aocLoanList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.loan",
                     permissionNames : [
                             "ROLE_PERM_AOC_LOAN_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_LOAN_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocLoanList.entities', null, 'aocLoanList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.loan",
                     permissionNames : [
                             "ROLE_PERM_AOC_LOAN_LIST_LIST_OUTGOING",
                     ]
                    ],
                    [groupName       : "GROUP_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocLoanNoticeReplayList.entities', null, 'aocLoanNoticeReplayList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.loan",
                     permissionNames : [
                             "ROLE_PERM_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocLoanNoticeReplayList.entities', null, 'aocLoanNoticeReplayList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.loan",
                     permissionNames : [
                             "ROLE_PERM_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_OUTGOING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_SUSPENSION_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocVacationList.entities', null, 'aocVacationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.suspension",
                     permissionNames : [
                             "ROLE_PERM_AOC_SUSPENSION_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_SUSPENSION_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocVacationList.entities', null, 'aocVacationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.suspension",
                     permissionNames : [
                             "ROLE_PERM_AOC_SUSPENSION_LIST_LIST_OUTGOING",
                     ]
                    ],


                    [groupName       : "GROUP_AOC_ALLOWANCE_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocAllowanceList.entities', null, 'aocAllowanceList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.allowance",
                     permissionNames : [
                             "ROLE_PERM_AOC_ALLOWANCE_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_ALLOWANCE_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocAllowanceList.entities', null, 'aocAllowanceList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.allowance",
                     permissionNames : [
                             "ROLE_PERM_AOC_ALLOWANCE_LIST_LIST_OUTGOING",
                     ]
                    ],


                    [groupName       : "GROUP_AOC_PROMOTION_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocPromotionList.entities', null, 'aocPromotionList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.promotion",
                     permissionNames : [
                             "ROLE_PERM_AOC_PROMOTION_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_PROMOTION_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocPromotionList.entities', null, 'aocPromotionList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.promotion",
                     permissionNames : [
                             "ROLE_PERM_AOC_PROMOTION_LIST_LIST_OUTGOING",
                     ]
                    ],



                    [groupName       : "GROUP_AOC_CHILD_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocChildList.entities', null, 'aocChildList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.child",
                     permissionNames : [
                             "ROLE_PERM_AOC_CHILD_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_CHILD_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocChildList.entities', null, 'aocChildList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.child",
                     permissionNames : [
                             "ROLE_PERM_AOC_CHILD_LIST_LIST_OUTGOING",
                     ]
                    ],





                    [groupName       : "GROUP_AOC_VIOLATION_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocViolationList.entities', null, 'aocViolationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.violation",
                     permissionNames : [
                             "ROLE_PERM_AOC_VIOLATION_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_VIOLATION_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocViolationList.entities', null, 'aocViolationList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.violation",
                     permissionNames : [
                             "ROLE_PERM_AOC_VIOLATION_LIST_LIST_OUTGOING",
                     ]
                    ],






                    [groupName       : "GROUP_AOC_MARITAL_STATUS_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocMaritalStatusList.entities', null, 'aocMaritalStatusList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_AOC_MARITAL_STATUS_LIST_LIST_INCOMING",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_FILTER_CAN_HAVE_OPERATION",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_MARITAL_STATUS_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocMaritalStatusList.entities', null, 'aocMaritalStatusList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_AOC_MARITAL_STATUS_LIST_LIST_OUTGOING",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_FILTER_CAN_HAVE_OPERATION",
                     ]
                    ],






                    [groupName       : "GROUP_AOC_EXTERNAL_TRANSFER_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocExternalTransferList.entities', null, 'aocExternalTransferList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.transfer",
                     permissionNames : [
                             "ROLE_PERM_AOC_EXTERNAL_TRANSFER_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_EXTERNAL_TRANSFER_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocExternalTransferList.entities', null, 'aocExternalTransferList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.transfer",
                     permissionNames : [
                             "ROLE_PERM_AOC_EXTERNAL_TRANSFER_LIST_LIST_OUTGOING",
                     ]
                    ],


                    [groupName       : "GROUP_AOC_DISCIPLINARY_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocDisciplinaryList.entities', null, 'aocDisciplinaryList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_AOC_DISCIPLINARY_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_DISCIPLINARY_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocDisciplinaryList.entities', null, 'aocDisciplinaryList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_AOC_DISCIPLINARY_LIST_LIST_OUTGOING",
                     ]
                    ],


                    [groupName       : "GROUP_AOC_DISPATCH_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocDispatchList.entities', null, 'aocDispatchList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.dispatch",
                     permissionNames : [
                             "ROLE_PERM_AOC_DISPATCH_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_DISPATCH_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocDispatchList.entities', null, 'aocDispatchList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.dispatch",
                     permissionNames : [
                             "ROLE_PERM_AOC_DISPATCH_LIST_LIST_OUTGOING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_RETURN_TO_SERVICE_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocReturnToServiceList.entities', null, 'aocReturnToServiceList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.returnToService",
                     permissionNames : [
                             "ROLE_PERM_AOC_RETURN_TO_SERVICE_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_RETURN_TO_SERVICE_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocReturnToServiceList.entities', null, 'aocReturnToServiceList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.returnToService",
                     permissionNames : [
                             "ROLE_PERM_AOC_RETURN_TO_SERVICE_LIST_LIST_OUTGOING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_END_OF_SERVICE_LIST_LIST_INCOMING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocEndOfServiceList.entities', null, 'aocEndOfServiceList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.endOfService",
                     permissionNames : [
                             "ROLE_PERM_AOC_END_OF_SERVICE_LIST_LIST_INCOMING",
                     ]
                    ],

                    [groupName       : "GROUP_AOC_END_OF_SERVICE_LIST_LIST_OUTGOING",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('aocEndOfServiceList.entities', null, 'aocEndOfServiceList', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.correspondences.endOfService",
                     permissionNames : [
                             "ROLE_PERM_AOC_END_OF_SERVICE_LIST_LIST_OUTGOING",
                     ]
                    ],

                    [groupName       : "GROUP_PROFILE_NOTICE_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('profileNotice.entities', null, 'Show Profile Notice', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profileNotice.ProfileNotice",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_CHANGE_EMPLOYEE_PROFILE_STATUS_MODAL",
                             "ROLE_PERM_PROFILE_NOTICE_END_NOTICE_MODAL",
                             "ROLE_PERM_PROFILE_NOTICE_LIST",
                     ]
                    ],

                    [groupName       : "GROUP_PROFILE_NOTICE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('profileNotice.entities', null, 'List Profile Notice', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profileNotice.ProfileNotice",
                     permissionNames : [
                             "ROLE_PERM_PROFILE_NOTICE_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PROFILE_NOTICE_FILTER",
                             "ROLE_PERM_PROFILE_NOTICE_SHOW",
                             "ROLE_PERM_PROFILE_NOTICE_EDIT",
                             "ROLE_PERM_PROFILE_NOTICE_DELETE",
                             "ROLE_PERM_PROFILE_NOTICE_CREATE",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PROFILE_NOTICE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('profileNotice.entities', null, 'Create Profile Notice', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profileNotice.ProfileNotice",
                     permissionNames : [
                             "ROLE_PERM_PROFILE_NOTICE_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_PROFILE_NOTICE_SAVE",
                     ]
                    ],

                    [groupName       : "GROUP_PROFILE_NOTICE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('profileNotice.entities', null, 'Create Profile Notice', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profileNotice.ProfileNotice",
                     permissionNames : [
                             "ROLE_PERM_PROFILE_NOTICE_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_PROFILE_NOTICE_UPDATE",
                     ]
                    ],

                    /**
                     * workflow -> show
                     */
                    [groupName       : "GROUP_WORKFLOW_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('workflow.entities', null, 'workflow', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.workflow",
                     permissionNames : [
                             "ROLE_PERM_WORKFLOW_STEP_LIST",
                             "ROLE_PERM_WORKFLOW_STEP_SHOW",
                             "ROLE_PERM_WORKFLOW_STEP_EDIT",
                             "ROLE_PERM_WORKFLOW_STEP_DELETE",
                             "ROLE_PERM_WORKFLOW_STEP_CREATE",
                             "ROLE_PERM_WORKFLOW_STEP_AUTO_COMPLETE_JOB_CATEGORY",
                             "ROLE_PERM_WORKFLOW_STEP_AUTO_COMPLETE_DEPARTMENT",
                             "ROLE_PERM_WORKFLOW_STEP_AUTO_COMPLETE_JOB_TITLE",
                             "ROLE_PERM_OPERATION_RELATED_WORKFLOW_LIST",
                             "ROLE_PERM_OPERATION_RELATED_WORKFLOW_DELETE",
                             "ROLE_PERM_OPERATION_RELATED_WORKFLOW_CREATE"
                     ]
                    ],

                    //report
                    [groupName       : "GROUP_REPORT_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('report.entities', null, 'report', arabicLocal),
                     groupCagtegory  : "ps.police.report",
                     permissionNames : [
                             "ROLE_PERM_REPORT_SHOW",
                             "ROLE_PERM_REPORT_SHOW_LIST",
                             "ROLE_PERM_REPORT_SHOW_MULTI_LIST",
                             "ROLE_PERM_REPORT_GENERATE_RESPONSE",
                             "ROLE_PERM_REPORT_SHOW_STATIC",
                             "ROLE_PERM_REPORT_STATIC_VIEW_LIST",
                     ]
                    ],

                    //audit
                    [groupName       : "GROUP_AUDIT_LOG_EVENT_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('auditLogEvent.list', null, 'audit log event list', arabicLocal),
                     groupCagtegory  : "ps.police.audit",
                     permissionNames : [
                             "ROLE_PERM_AUDIT_LOG_EVENT_LIST_DETAILS",
                             "ROLE_PERM_AUDIT_LOG_EVENT_SHOW",
                     ]
                    ],

                    //view_cache
                    [groupName       : "GROUP_VIEW_CACHE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('auditLogEvent.list', null, 'audit log event list', arabicLocal),
                     groupCagtegory  : "ps.police.audit",
                     permissionNames : [
                             "ROLE_PERM_VIEW_CACHE_MODAL_SELECT",
                             "ROLE_PERM_VIEW_CACHE_RENDER_SAVED_DATA_TABLE_FIELD_INPUT",
                             "ROLE_PERM_VIEW_CACHE_RENDER_SEARCH_FIELD_INPUT",
                             "ROLE_PERM_VIEW_CACHE_RENDER_VALUE_FIELD_OPERATOR",
                             "ROLE_PERM_VIEW_CACHE_RENDER_FIELD_BY_TYPE",
                             "ROLE_PERM_VIEW_CACHE_RENDER_SAVED_SEARCH_FIELD_INPUT",
                             "ROLE_PERM_VIEW_CACHE_MODAL_ADVANCE_SEARCH",
                             "ROLE_PERM_VIEW_CACHE_SAVE_DATA_TABLE_CONTROL",
                             "ROLE_PERM_VIEW_CACHE_MODAL_CREATE",
                             "ROLE_PERM_VIEW_CACHE_RENDER_ORDER_FIELD_INPUT",
                             "ROLE_PERM_VIEW_CACHE_GET_INSTANCE",
                             "ROLE_PERM_VIEW_CACHE_RENDER_SAVED_ORDER_FIELD_INPUT",
                             "ROLE_PERM_VIEW_CACHE_AUTOCOMPLETE",
                     ]
                    ],

                    //systemReport
                    [groupName       : "GROUP_SYSTEM_REPORT_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('systemReport.entities', null, 'systemReport', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.common",
                     permissionNames : [
                             "ROLE_PERM_SYSTEM_REPORT_EMPLOYEE_RANK",
                             "ROLE_PERM_SYSTEM_REPORT_EMPLOYEE_RANK_FILTER",
                             "ROLE_PERM_SYSTEM_REPORT_RENDER_REPORT",
                     ]
                    ],

                    //REQUEST -> LIST
                    [groupName       : "GROUP_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('request.entities', null, 'request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_REQUEST_FILTER_WORKFLOW_REQUEST",
                             "ROLE_PERM_REQUEST_MANAGE_REQUEST_MODAL"

                     ]
                    ],

                    //REQUEST -> EDIT, STOP, CANCEL AND EXTEND OPERATIONS
                    [groupName       : "GROUP_REQUEST_MANAGE_OPERATIONS",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('request.manage.operations.group.label', null, 'request manage operations', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [
                             "ROLE_PERM_REQUEST_STOP_REQUEST_CREATE",
                             "ROLE_PERM_REQUEST_EXTEND_REQUEST_CREATE",
                             "ROLE_PERM_REQUEST_CANCEL_REQUEST_CREATE",
                             "ROLE_PERM_REQUEST_EDIT_REQUEST_CREATE",
                             "ROLE_PERM_REQUEST_SAVE_OPERATION",
                     ]
                    ],

                    //REQUEST -> EDIT, STOP, CANCEL AND EXTEND OPERATIONS
                    [groupName       : "GROUP_REQUEST_SET_MANAGERIAL_ORDER_INFO",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('request.set.managerial.info.group.label', null, 'request set managerial info', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [
                             "ROLE_PERM_REQUEST_SET_INTERNAL_MANAGERIAL_ORDER",
                             "ROLE_PERM_REQUEST_SAVE_INTERNAL_MANAGERIAL_ORDER",
                     ]
                    ],

                    //attachment
                    [groupName       : "GROUP_SYSTEM_ATTACHMENT",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('attachment.entities', null, 'attachment', arabicLocal),
                     groupCagtegory  : "ps.epsilon.attach",
                     permissionNames : [
                             "ROLE_PERM_ATTACHMENT_PREVIEW_ATTACHMENT_FILE",
                             "ROLE_PERM_ATTACHMENT_DELETE_ATTACHMENT_FILE",
                             "ROLE_PERM_ATTACHMENT_DOWNLOAD_SINGLE_ATTACHMENT_FILE",
                             "ROLE_PERM_ATTACHMENT_SHOW_ATTACHMENT_DATA",
                             "ROLE_PERM_ATTACHMENT_CREATE_ATTACHMENT",
                             "ROLE_PERM_ATTACHMENT_DOWNLOAD_ALL_ATTACHMENT_FILE",
                             "ROLE_PERM_ATTACHMENT_EDIT_ATTACHMENT",
                             "ROLE_PERM_ATTACHMENT_GET_SESSION",
                             "ROLE_PERM_ATTACHMENT_GET_ATTACHMENT_DATA_AS_JSON",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                     ]
                    ],

                    //tabs
                    [groupName       : "GROUP_SYSTEM_TABS",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('tabs.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "tabs",
                     permissionNames : [
                             "ROLE_PERM_TABS_SHOW_IN_LINE",
                             "ROLE_PERM_TABS_EDIT_IN_LINE",
                             "ROLE_PERM_TABS_LIST_IN_LINE",
                             "ROLE_PERM_TABS_CREATE_IN_LINE",
                             "ROLE_PERM_TABS_LOAD_TAB"
                     ]
                    ],
                    //core tabs
                    [groupName       : "GROUP_SYSTEM_PCORE_TABS",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('pcoreTabs.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "pcoreTabs",
                     permissionNames : [
                             "ROLE_PERM_PCORE_TABS_SHOW_IN_LINE",
                             "ROLE_PERM_PCORE_TABS_EDIT_IN_LINE",
                             "ROLE_PERM_PCORE_TABS_LIST_IN_LINE",
                             "ROLE_PERM_PCORE_TABS_CREATE_IN_LINE",
                             "ROLE_PERM_PCORE_TABS_LOAD_TAB"
                     ]
                    ],

                    //contact info
                    [groupName       : "GROUP_CONTACT_INFO_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('contactInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_CONTACT_METHOD_AUTOCOMPLETE",
                             "ROLE_PERM_CONTACT_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_CONTACT_INFO_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('contactInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_CONTACT_METHOD_AUTOCOMPLETE",
                             "ROLE_PERM_CONTACT_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    //legal identifier
                    [groupName       : "GROUP_LEGAL_IDENTIFIER_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('legalIdentifier.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_DOCUMENT_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_DOCUMENT_CLASSIFICATION_AUTOCOMPLETE",
                             "ROLE_PERM_LEGAL_IDENTIFIER_LEVEL_AUTOCOMPLETE",
                             "ROLE_PERM_LEGAL_IDENTIFIER_RESTRICTION_AUTOCOMPLETE",
                             "ROLE_PERM_COUNTRY_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_LEGAL_IDENTIFIER_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('legalIdentifier.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_DOCUMENT_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_DOCUMENT_CLASSIFICATION_AUTOCOMPLETE",
                             "ROLE_PERM_LEGAL_IDENTIFIER_LEVEL_AUTOCOMPLETE",
                             "ROLE_PERM_LEGAL_IDENTIFIER_RESTRICTION_AUTOCOMPLETE",
                             "ROLE_PERM_COUNTRY_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    //person education
                    [groupName       : "GROUP_PERSON_EDUCATION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personEducation.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_EDUCATION_DEGREE_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_MAJOR_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_EDUCATION_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personEducation.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_EDUCATION_DEGREE_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_MAJOR_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    //person employment history
                    [groupName       : "GROUP_PERSON_EMPLOYMENT_HISTORY_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personEmploymentHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_PROFESSION_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_EMPLOYMENT_HISTORY_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personEmploymentHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_PROFESSION_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    //person arrest history
                    [groupName       : "GROUP_PERSON_ARREST_HISTORY_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personArrestHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_JAIL_AUTOCOMPLETE",
                             "ROLE_PERM_UNIT_OF_MEASUREMENT_AUTOCOMPLETE",
                     ]
                    ],

                    //person arrest history
                    [groupName       : "GROUP_PERSON_ARREST_HISTORY_PRE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personArrestHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_PERSON_ARREST_HISTORY_CREATE",
                             "ROLE_PERM_PERSON_ARREST_HISTORY_LIST",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_ARREST_HISTORY_SELECT_EMPLOYEE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_ARREST_HISTORY_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personArrestHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_JAIL_AUTOCOMPLETE",
                             "ROLE_PERM_UNIT_OF_MEASUREMENT_AUTOCOMPLETE",
                     ]
                    ],

                    //person health history
                    [groupName       : "GROUP_PERSON_HEALTH_HISTORY_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personHealthHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_DISEASE_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_HEALTH_HISTORY_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personHealthHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_DISEASE_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    //person relationship
                    [groupName       : "GROUP_PERSON_RELATION_SHIPS_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personRelationShips.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_RELATIONSHIP_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_RELATION_SHIPS_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personRelationShips.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_RELATIONSHIP_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    //person language info
                    [groupName       : "GROUP_PERSON_LANGUAGE_INFO_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personLanguageInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_LANGUAGE_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_LANGUAGE_INFO_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personLanguageInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_LANGUAGE_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    //person language info
                    [groupName       : "GROUP_PERSON_LANGUAGE_INFO_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personLanguageInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_LANGUAGE_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_LANGUAGE_INFO_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personLanguageInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_LANGUAGE_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    //person training history
                    [groupName       : "GROUP_PERSON_TRAINING_HISTORY_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personTrainingHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_TRAINING_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_TRAINING_DEGREE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_TRAINING_HISTORY_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personTrainingHistory.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_TRAINING_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_TRAINING_DEGREE_AUTOCOMPLETE",
                     ]
                    ],

                    //person nationality
                    [groupName       : "GROUP_PERSON_NATIONALITY_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personNationality.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_COUNTRY_AUTOCOMPLETE",
                             "ROLE_PERM_NATIONALITY_ACQUISITION_METHOD_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_NATIONALITY_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personNationality.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_COUNTRY_AUTOCOMPLETE",
                             "ROLE_PERM_NATIONALITY_ACQUISITION_METHOD_AUTOCOMPLETE",
                     ]
                    ],

                    //person characteristics
                    [groupName       : "GROUP_PERSON_CHARACTERISTICS_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personCharacteristics.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_HAIR_FEATURE_AUTOCOMPLETE",
                             "ROLE_PERM_COLOR_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_CHARACTERISTICS_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personCharacteristics.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_HAIR_FEATURE_AUTOCOMPLETE",
                             "ROLE_PERM_COLOR_AUTOCOMPLETE",
                     ]
                    ],

                    //person characteristics
                    [groupName       : "GROUP_PERSON_DISABILITY_INFO_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personDisabilityInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_DISABILITY_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_DISABILITY_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_PERSON_DISABILITY_INFO_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('personDisabilityInfo.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_DISABILITY_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_DISABILITY_LEVEL_AUTOCOMPLETE",
                     ]
                    ],

                    //employee
                    [groupName       : "GROUP_EMPLOYEE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employee.entity', null, 'employee', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profile",
                     permissionNames : [
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_CREATE_NEW_PERSON",
                             "ROLE_PERM_EMPLOYEE_CREATE_NEW_EMPLOYEE",
                             "ROLE_PERM_EMPLOYEE_SAVE_NEW_PERSON",
                             "ROLE_PERM_EMPLOYEE_GET_PERSON",
                             "ROLE_PERM_DEPARTMENT_GET_INSTANCE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_LOCATION_GET_LOCATION_INFO",
                             "ROLE_PERM_PCORE_REGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_DISTRICT_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOOD_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_RELIGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_ETHNICITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GENDER_TYPE_AUTO_COMPLETE",
                     ]
                    ],
                    //employee
                    [groupName       : "GROUP_EMPLOYEE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employee.entities', null, 'employees', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profile",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_FILTER_IDS",
                     ]
                    ],

                    //employee
                    [groupName       : "GROUP_EMPLOYEE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employee.entity', null, 'employee', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profile",
                     permissionNames : [
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                     ]
                    ],

                    //employee
                    [groupName       : "GROUP_EMPLOYEE_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employee.entity', null, 'employee', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profile",
                     permissionNames : [
                             "ROLE_PERM_REPORT_SHOW_MULTI_LIST",
                     ]
                    ],

                    //employee external assignation
                    [groupName       : "GROUP_EMPLOYEE_EXTERNAL_ASSIGNATION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeExternalAssignation.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profile",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_EMPLOYEE_EXTERNAL_ASSIGNATION_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeExternalAssignation.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profile",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    //employee violation
                    [groupName       : "GROUP_EMPLOYEE_VIOLATION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeViolation.entity', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_VIOLATION_SELECT_EMPLOYEE",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_CREATE_NEW_EMPLOYEE_VIOLATION",
                             "ROLE_PERM_DISCIPLINARY_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_LOCATION_GET_LOCATION_INFO",
                             "ROLE_PERM_PCORE_REGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_DISTRICT_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_EMPLOYEE_VIOLATION_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeViolation.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_VIOLATION_SELECT_EMPLOYEE",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_CREATE_NEW_EMPLOYEE_VIOLATION",
                             "ROLE_PERM_DISCIPLINARY_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_LOCATION_GET_LOCATION_INFO",
                             "ROLE_PERM_PCORE_REGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_DISTRICT_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_EMPLOYEE_VIOLATION_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeViolation.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_REASON_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                     ]
                    ],

                    //disciplinary request
                    [groupName       : "GROUP_DISCIPLINARY_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryRequest.entity', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_CREATE_NEW_DISCIPLINARY_REQUEST",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_GET_DISCIPLINARY_REASONS",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_GET_DISCIPLINARY_JUDGMENTS_INPUTS",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_GET_VIOLATIONS_WITH_JUDGMENTS",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_PREVIOUS_JUDGMENTS_MODAL",
                             "ROLE_PERM_DISCIPLINARY_RECORD_JUDGMENT_FILTER",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_LIST_MODAL",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_FILTER",
                             "ROLE_PERM_DISCIPLINARY_REASON_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_CURRENCY_AUTOCOMPLETE",
                             "ROLE_PERM_UNIT_OF_MEASUREMENT_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_DISCIPLINARY_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_REQUEST_GET_DISCIPLINARY_REASONS",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_GET_DISCIPLINARY_JUDGMENTS_INPUTS",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_GET_VIOLATIONS_WITH_JUDGMENTS",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_PREVIOUS_JUDGMENTS_MODAL",
                             "ROLE_PERM_DISCIPLINARY_RECORD_JUDGMENT_FILTER",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_LIST_MODAL",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_FILTER",
                             "ROLE_PERM_DISCIPLINARY_REASON_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_CURRENCY_AUTOCOMPLETE",
                             "ROLE_PERM_UNIT_OF_MEASUREMENT_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_DISCIPLINARY_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_REQUEST_SHOW_DETAILS",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                             "ROLE_PERM_PETITION_REQUEST_AUTOCOMPLETE",
                             "ROLE_PERM_PETITION_REQUEST_CREATE_NEW_REQUEST",
                             "ROLE_PERM_PETITION_REQUEST_SHOW_RELATED_REQUEST",
                     ]
                    ],

                    [groupName       : "GROUP_DISCIPLINARY_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_LIST_MANAGE_DISCIPLINARY_LIST",
                             "ROLE_PERM_DISCIPLINARY_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_DISCIPLINARY_LIST_SEND_LIST",
                             "ROLE_PERM_DISCIPLINARY_LIST_ADD_DISCIPLINARY_RECORD_JUDGMENT_MODAL",
                             "ROLE_PERM_DISCIPLINARY_LIST_ADD_DISCIPLINARY_RECORD_JUDGMENT",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_RECORD_JUDGMENT_FILTER",
                             "ROLE_PERM_DISCIPLINARY_RECORD_JUDGMENT_DELETE",
                             "ROLE_PERM_DISCIPLINARY_RECORD_JUDGMENT_SHOW",
                             "ROLE_PERM_DISCIPLINARY_RECORD_JUDGMENT_SHOW_DETAILS",
                             "ROLE_PERM_CURRENCY_AUTOCOMPLETE",
                             "ROLE_PERM_UNIT_OF_MEASUREMENT_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_DISCIPLINARY_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryList.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    [groupName       : "GROUP_DISCIPLINARY_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryList.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    //internal transfer request
                    [groupName       : "GROUP_INTERNAL_TRANSFER_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('internalTransferRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_INTERNAL_TRANSFER_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_INTERNAL_TRANSFER_REQUEST_CREATE_NEW_INTERNAL_TRANSFER_REQUEST",
                     ]
                    ],

                    [groupName       : "GROUP_INTERNAL_TRANSFER_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('internalTransferRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_GOVERNORATE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_INTERNAL_TRANSFER_REQUEST_CLOSE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('internalTransferRequest.closeRequest.label', null, 'close request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_INTERNAL_TRANSFER_REQUEST_CLOSE",
                             "ROLE_PERM_INTERNAL_TRANSFER_REQUEST_SAVE_CLOSE",
                     ]
                    ],

                    //external transfer request
                    [groupName       : "GROUP_EXTERNAL_TRANSFER_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_CREATE_NEW_EXTERNAL_TRANSFER_REQUEST",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_PROVINCE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_EXTERNAL_TRANSFER_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_PROVINCE_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_EXTERNAL_TRANSFER_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_GOVERNORATE_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_GO_TO_LIST",
                     ]
                    ],

                    [groupName       : "GROUP_EXTERNAL_TRANSFER_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferRequest.entity', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_GO_TO_LIST",
                     ]
                    ],

                    [groupName       : "GROUP_CLEARANCE_MANAGEMENT",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferRequest.clearanceInfo.label', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_ADD_CLEARANCE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_SAVE_CLEARANCE",
                     ]
                    ],

                    [groupName       : "GROUP_TRANSFER_PERMISSION_MANAGEMENT",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferRequest.transferInfo.label', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_ADD_TRANSFER",
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_SAVE_TRANSFER",
                     ]
                    ],
                    [groupName       : "GROUP_EXTERNAL_TRANSFER_REQUEST_CLOSE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferRequest.closeRequest.label', null, 'close request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_CLOSE_REQUEST",
                     ]
                    ],
                    [groupName       : "GROUP_EXTERNAL_TRANSFER_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_FILTER",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_EMPLOYEE_DELETE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_MANAGE_EXTERNAL_TRANSFER_LIST",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_ADD_EXTERNAL_TRANSFER_REQUESTS_MODAL",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_ADD_EXTERNAL_TRANSFER_REQUESTS",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_SEND_DATA_MODAL",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_SEND_DATA",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_RECEIVE_DATA_MODAL",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_RECEIVE_DATA",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_ADD_EXCEPTIONAL_MODAL",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_REJECT_REQUEST",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_CLOSE_MODAL",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_CLOSE_LIST",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_NOTE_LIST",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_NOTE_CREATE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_EMPLOYEE_NOTE_SAVE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_GOVERNORATE_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_EXTERNAL_TRANSFER_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferList.entity', null, 'externalTransferList list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    [groupName       : "GROUP_EXTERNAL_TRANSFER_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalTransferList.entity', null, 'externalTransferList list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    //external received transferred person
                    [groupName       : "GROUP_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalReceivedTransferredPerson.entities', null, 'external received transferred person', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalReceivedTransferredPerson.label', null, 'external received transferred person', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_CREATE_NEW_PERSON",
                             "ROLE_PERM_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_CREATE_NEW_EXTERNAL_RECEIVED",
                             "ROLE_PERM_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_SAVE_NEW_PERSON",
                             "ROLE_PERM_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_GET_PERSON",
                             "ROLE_PERM_LOCATION_GET_LOCATION_INFO",
                             "ROLE_PERM_PCORE_REGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_DISTRICT_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('externalReceivedTransferredPerson.label', null, 'external received transferred person', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.transfer",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    //applicant
                    [groupName       : "GROUP_APPLICANT_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicant.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_SAVE_NEW_PERSON",
                             "ROLE_PERM_APPLICANT_CREATE_NEW_PERSON",
                             "ROLE_PERM_APPLICANT_CREATE_NEW_APPLICANT",
                             "ROLE_PERM_APPLICANT_SELECT_PERSON",
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_LOCATION_GET_LOCATION_INFO",
                             "ROLE_PERM_APPLICANT_PERSON_APPLICANT_PROFILES_MODAL",
                             "ROLE_PERM_PCORE_REGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_DISTRICT_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_APPLICANT_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicant.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_APPLICANT_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicant.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_GET_INTERVIEW",
                             "ROLE_PERM_APPLICANT_GO_TO_LIST_TRAINEE",
                             "ROLE_PERM_APPLICANT_GO_TO_LIST_RECRUITMENT",
                     ]
                    ],

                    [groupName       : "GROUP_APPLICANT_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicant.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_GET_INTERVIEW",
                             "ROLE_PERM_APPLICANT_GO_TO_LIST_TRAINEE",
                             "ROLE_PERM_APPLICANT_GO_TO_LIST_RECRUITMENT",
                             "ROLE_PERM_APPLICANT_DELETE",
                     ]
                    ],

                    //jobRequisition
                    [groupName       : "GROUP_JOB_REQUISITION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequisition.entity', null, 'jobRequisition', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_JOB_REQUISITION_GET_MANDATORY_INSPECTION",
                             "ROLE_PERM_JOB_REQUISITION_PREVIOUS_WORK_MODAL",
                             "ROLE_PERM_JOB_REQUISITION_GET_JOB_INFORMATION",
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_JOB_REQUISITION_CREATE_MANAGER",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequisition.entity', null, 'jobRequisition', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_JOB_REQUISITION_GET_MANDATORY_INSPECTION",
                             "ROLE_PERM_JOB_REQUISITION_PREVIOUS_WORK_MODAL",
                             "ROLE_PERM_JOB_REQUISITION_GET_JOB_INFORMATION",
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                     ]
                    ],

                    //jobRequisition
                    [groupName       : "GROUP_JOB_REQUISITION_UPDATE_MANAGER",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequisition.entity', null, 'jobRequisition', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_JOB_REQUISITION_GET_MANDATORY_INSPECTION",
                             "ROLE_PERM_JOB_REQUISITION_PREVIOUS_WORK_MODAL",
                             "ROLE_PERM_JOB_REQUISITION_GET_JOB_INFORMATION",
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_JOB_REQUISITION_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequisition.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_JOB_REQUISITION_GET_MANDATORY_INSPECTION",
                             "ROLE_PERM_JOB_REQUISITION_PREVIOUS_WORK_MODAL",
                             "ROLE_PERM_JOB_REQUISITION_GET_JOB_INFORMATION",
                             "ROLE_PERM_JOB_REQUISITION_AUTO_COMPLETE_OPENED_RECRUITMENT_CYCLE",
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                     ]
                    ],


                    [groupName       : "GROUP_JOB_REQUISITION_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequisition.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_JOB_REQUISITION_LIST_MANAGER",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequisition.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_JOB_REQUISITION_LIST_MANAGER",
                             "ROLE_PERM_JOB_REQUISITION_SHOW_MANAGER",
                             "ROLE_PERM_JOB_REQUISITION_EDIT_MANAGER",
                             "ROLE_PERM_JOB_REQUISITION_SET_APPROVED_POSITIONS",
                             "ROLE_PERM_JOB_REQUISITION_FILTER_MANAGER",
                             "ROLE_PERM_JOB_REQUISITION_ACCEPT_FORM_MODAL",
                             "ROLE_PERM_JOB_REQUISITION_REJECT_FORM_MODAL",
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTOCOMPLETE",
                     ]
                    ],

                    //vacancy
                    [groupName       : "GROUP_VACANCY_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacancy.entity', null, 'vacancy', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_VACANCY_GET_MANDATORY_INSPECTION",
                             "ROLE_PERM_VACANCY_AUTO_COMPLETE_VACANCY_RECRUITMENT_CYCLE",
                             "ROLE_PERM_VACANCY_GET_JOB_REQUISITION_INFO",
                             "ROLE_PERM_VACANCY_GET_THE_SAME_JOB_REQUISITION_NAME",
                             "ROLE_PERM_VACANCY_PREVIOUS_WORK_MODAL",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_VACANCY_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacancy.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_VACANCY_GET_MANDATORY_INSPECTION",
                             "ROLE_PERM_VACANCY_AUTO_COMPLETE_VACANCY_RECRUITMENT_CYCLE",
                             "ROLE_PERM_VACANCY_GET_JOB_REQUISITION_INFO",
                             "ROLE_PERM_VACANCY_GET_THE_SAME_JOB_REQUISITION_NAME",
                             "ROLE_PERM_VACANCY_PREVIOUS_WORK_MODAL",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_PROFESSION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                     ]
                    ],


                    [groupName       : "GROUP_VACANCY_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacancy.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                             "ROLE_PERM_JOB_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                     ]
                    ],

                    //trainee list , create
                    [groupName       : "GROUP_TRAINEE_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('traineeList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_TRAINEE_LIST_MANAGE_TRAINEE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    //trainee list , update
                    [groupName       : "GROUP_TRAINEE_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('traineeList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_TRAINEE_LIST_MANAGE_TRAINEE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    //trainee list
                    [groupName       : "GROUP_TRAINEE_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('traineeList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_FILTER_APPLICANT",
                             "ROLE_PERM_TRAINEE_LIST_MANAGE_TRAINEE_LIST",
                             "ROLE_PERM_TRAINEE_LIST_GET_TRAINEE_LIST_ID",
                             "ROLE_PERM_TRAINEE_LIST_FILTER_APPLICANT_TO_ADD",
                             "ROLE_PERM_TRAINEE_LIST_FILTER_APPLICANT",
                             "ROLE_PERM_TRAINEE_LIST_FILTER_APPLICANT_TO_ADD_AS_EXCEPTIONAL",
                             "ROLE_PERM_TRAINEE_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_TRAINEE_LIST_ADD_ELIGIBLE_APPLICANTS_MODAL",
                             "ROLE_PERM_TRAINEE_LIST_ADD_EXCEPTION_APPLICANTS_MODAL",
                             "ROLE_PERM_TRAINEE_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_TRAINEE_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_TRAINEE_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_TRAINEE_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_TRAINEE_LIST_SEND_LIST",
                             "ROLE_PERM_TRAINEE_LIST_RECEIVE_LIST",
                             "ROLE_PERM_TRAINEE_LIST_CHANGE_APPLICANT_TO_TRAINING_PASSED",
                             "ROLE_PERM_TRAINEE_LIST_CHANGE_APPLICANT_TO_REJECTED",
                             "ROLE_PERM_TRAINEE_LIST_CLOSE_LIST",
                             "ROLE_PERM_TRAINEE_LIST_ADD_APPLICANTS",
                             "ROLE_PERM_TRAINEE_LIST_ADD_EXCEPTIONAL_APPLICANTS",
                             "ROLE_PERM_TRAINEE_LIST_INSPECTION_LIST",
                             "ROLE_PERM_TRAINEE_LIST_NOTE_LIST",
                             "ROLE_PERM_TRAINEE_LIST_NOTE_CREATE",
                             "ROLE_PERM_TRAINEE_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_TRAINEE_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_TRAINEE_LIST_EMPLOYEE_NOTE_SAVE",
                             "ROLE_PERM_VACANCY_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * recruitment list ->  recruitment create
                     */
                    [groupName       : "GROUP_RECRUITMENT_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('recruitmentList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_LIST_MANAGE_RECRUITMENT_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    /**
                     * recruitment list ->  recruitment update
                     */
                    [groupName       : "GROUP_RECRUITMENT_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('recruitmentList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_LIST_MANAGE_RECRUITMENT_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    //recruitment list
                    [groupName       : "GROUP_RECRUITMENT_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('recruitmentList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_FILTER_APPLICANT",
                             "ROLE_PERM_RECRUITMENT_LIST_MANAGE_RECRUITMENT_LIST",
                             "ROLE_PERM_RECRUITMENT_LIST_GET_RECRUITMENT_LIST_ID",
                             "ROLE_PERM_RECRUITMENT_LIST_FILTER_APPLICANT_TO_ADD",
                             "ROLE_PERM_RECRUITMENT_LIST_FILTER_APPLICANT",
                             "ROLE_PERM_RECRUITMENT_LIST_FILTER_APPLICANT_TO_ADD_AS_EXCEPTIONAL",
                             "ROLE_PERM_RECRUITMENT_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_RECRUITMENT_LIST_ADD_ELIGIBLE_APPLICANTS_MODAL",
                             "ROLE_PERM_RECRUITMENT_LIST_ADD_EXCEPTION_APPLICANTS_MODAL",
                             "ROLE_PERM_RECRUITMENT_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_RECRUITMENT_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_RECRUITMENT_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_RECRUITMENT_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_RECRUITMENT_LIST_SEND_LIST",
                             "ROLE_PERM_RECRUITMENT_LIST_RECEIVE_LIST",
                             "ROLE_PERM_RECRUITMENT_LIST_CHANGE_APPLICANT_TO_EMPLOYED",
                             "ROLE_PERM_RECRUITMENT_LIST_CHANGE_APPLICANT_TO_REJECTED",
                             "ROLE_PERM_RECRUITMENT_LIST_CLOSE_LIST",
                             "ROLE_PERM_RECRUITMENT_LIST_ADD_APPLICANTS",
                             "ROLE_PERM_RECRUITMENT_LIST_ADD_EXCEPTIONAL_APPLICANTS",
                             "ROLE_PERM_RECRUITMENT_LIST_INSPECTION_LIST",
                             "ROLE_PERM_RECRUITMENT_LIST_NOTE_LIST",
                             "ROLE_PERM_RECRUITMENT_LIST_NOTE_CREATE",
                             "ROLE_PERM_RECRUITMENT_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_RECRUITMENT_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_RECRUITMENT_LIST_EMPLOYEE_NOTE_SAVE",
                             "ROLE_PERM_VACANCY_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_CREATE_NEW_EMPLOYEE"
                     ]
                    ],

                    //applicant inspection category result
                    [groupName       : "GROUP_APPLICANT_INSPECTION_CATEGORY_RESULT_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicantInspectionCategoryResult.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_GET_INSPECTION_CATEGORY",
                             "ROLE_PERM_APPLICANT_GET_INSPECTION_CATEGORY_BY_APPLICANT",
                     ]
                    ],
                    [groupName       : "GROUP_APPLICANT_INSPECTION_CATEGORY_RESULT_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicantInspectionCategoryResult.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_GET_INSPECTION_CATEGORY",
                             "ROLE_PERM_APPLICANT_GET_INSPECTION_CATEGORY_BY_APPLICANT",
                     ]
                    ],
                    //recruitment cycle
                    [groupName       : "GROUP_RECRUITMENT_CYCLE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('recruitmentCycle.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_CYCLE_CHANGE_PHASE",
                             "ROLE_PERM_RECRUITMENT_CYCLE_MANAGE_DEPARTMENTS",
                             "ROLE_PERM_RECRUITMENT_CYCLE_UPDATE_RECRUITMENT_CYCLE_DEPARTMENT",
                             "ROLE_PERM_RECRUITMENT_CYCLE_ADD_JOB_REQUISITION",
                             "ROLE_PERM_RECRUITMENT_CYCLE_JOINED_RECRUITMENT_CYCLE_DEPARTMENT",
                             "ROLE_PERM_JOB_REQUISITION_FILTER_JOB_REQUISITION_TO_ADD",
                     ]
                    ],
                    [groupName       : "GROUP_RECRUITMENT_CYCLE_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('recruitmentCycle.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_CYCLE_JOINED_RECRUITMENT_CYCLE_DEPARTMENT_LIST",
                             "ROLE_PERM_RECRUITMENT_CYCLE_JOINED_RECRUITMENT_CYCLE_DEPARTMENT_FILTER",
                             "ROLE_PERM_JOB_REQUISITION_LIST",
                             "ROLE_PERM_JOB_REQUISITION_FILTER",
                             "ROLE_PERM_RECRUITMENT_CYCLE_PHASE_LIST",
                             "ROLE_PERM_RECRUITMENT_CYCLE_PHASE_FILTER",
                     ]
                    ],

                    /**
                     * suspension request -> list
                     */
                    [groupName       : "GROUP_SUSPENSION_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionRequest.entities', null, 'suspension request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_CREATE_RETURN_TO_SERVICE",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_SUSPENSION_REQUEST_EXTENSION_REQUEST_LIST",
                             "ROLE_PERM_SUSPENSION_REQUEST_EXTENSION_REQUEST_CREATE",
                             "ROLE_PERM_SUSPENSION_REQUEST_EXTENSION_REQUEST_EDIT",
                             "ROLE_PERM_SUSPENSION_REQUEST_EXTENSION_REQUEST_SHOW",
                             "ROLE_PERM_SUSPENSION_EXTENSION_REQUEST_DELETE",

                     ]
                    ],

                    /**
                     * suspension request -> create
                     */
                    [groupName       : "GROUP_SUSPENSION_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionRequest.entities', null, 'suspension request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_SUSPENSION_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_SUSPENSION_REQUEST_CREATE_NEW_SUSPENSION_REQUEST",
                             "ROLE_PERM_SUSPENSION_REQUEST_PREVIOUS_SUSPENSIONS_MODAL"

                     ]
                    ],
                    /**
                     * suspension request -> update
                     */
                    [groupName       : "GROUP_SUSPENSION_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionRequest.entities', null, 'suspension request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_REQUEST_PREVIOUS_SUSPENSIONS_MODAL"

                     ]
                    ],

                    /**
                     * suspension request -> show
                     */
                    [groupName       : "GROUP_SUSPENSION_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionRequest.entity', null, 'suspension request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_REQUEST_GO_TO_LIST",
                     ]
                    ],

                    /**
                     * suspension list ->  suspension list
                     */
                    [groupName       : "GROUP_SUSPENSION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionList.entities', null, 'suspension list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_LIST_MANAGE_SUSPENSION_LIST",
                             "ROLE_PERM_SUSPENSION_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_SUSPENSION_LIST_ADD_SUSPENSION_REQUESTS_MODAL",
                             "ROLE_PERM_SUSPENSION_REQUEST_FILTER",
                             "ROLE_PERM_SUSPENSION_LIST_ADD_SUSPENSION_REQUESTS",
                             "ROLE_PERM_SUSPENSION_LIST_SEND_DATA_MODAL",
                             "ROLE_PERM_SUSPENSION_LIST_SEND_DATA",
                             "ROLE_PERM_SUSPENSION_LIST_RECEIVE_DATA_MODAL",
                             "ROLE_PERM_SUSPENSION_LIST_RECEIVE_DATA",
                             "ROLE_PERM_SUSPENSION_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_SUSPENSION_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_SUSPENSION_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_SUSPENSION_LIST_REJECT_REQUEST",
                             "ROLE_PERM_SUSPENSION_LIST_CLOSE_MODAL",
                             "ROLE_PERM_SUSPENSION_LIST_CLOSE_LIST",
                             "ROLE_PERM_SUSPENSION_LIST_NOTE_LIST",
                             "ROLE_PERM_SUSPENSION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_SUSPENSION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_SUSPENSION_LIST_NOTE_CREATE",
                             "ROLE_PERM_SUSPENSION_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * suspension list ->  suspension create
                     */
                    [groupName       : "GROUP_SUSPENSION_EXTENSION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionExtensionList.entities', null, 'suspension list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_MANAGE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * suspension list ->  suspension update
                     */
                    [groupName       : "GROUP_SUSPENSION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionList.entities', null, 'suspension list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_LIST_MANAGE_SUSPENSION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * vacation request -> list
                     */
                    [groupName       : "GROUP_VACATION_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationRequest.entities', null, 'vacation request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_REQUEST_EXTENSION_REQUEST_LIST",
                             "ROLE_PERM_VACATION_REQUEST_EXTENSION_REQUEST_CREATE",
                             "ROLE_PERM_VACATION_REQUEST_EXTENSION_REQUEST_EDIT",
                             "ROLE_PERM_VACATION_REQUEST_EXTENSION_REQUEST_SHOW",
                             "ROLE_PERM_VACATION_EXTENSION_REQUEST_FILTER",
                             "ROLE_PERM_VACATION_EXTENSION_REQUEST_LIST",
                             "ROLE_PERM_VACATION_EXTENSION_REQUEST_DELETE",
                             "ROLE_PERM_VACATION_REQUEST_STOP_REQUEST_CREATE",
                             "ROLE_PERM_STOP_VACATION_REQUEST_SAVE",
                             "ROLE_PERM_STOP_VACATION_REQUEST_LIST",
                             "ROLE_PERM_VACATION_REQUEST_SHOW_THREAD",
                     ]
                    ],

                    /**
                     * vacation request -> create
                     */
                    [groupName       : "GROUP_VACATION_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationRequest.entities', null, 'vacation request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_VACATION_REQUEST_CREATE_NEW_VACATION_REQUEST",
                             "ROLE_PERM_VACATION_REQUEST_CREATE_NEW_LIST_VACATION_REQUEST",
                             "ROLE_PERM_PCORE_REGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_DISTRICT_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOOD_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_RELIGION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_ETHNICITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_COMPETENCY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GENDER_TYPE_AUTO_COMPLETE",
                     ]
                    ],

                    /**
                     * vacation request -> create new vacation request
                     */
                    [groupName       : "GROUP_VACATION_REQUEST_CREATE_NEW_VACATION_REQUEST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationRequest.entities', null, 'vacation request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                             "ROLE_PERM_VACATION_REQUEST_SELECTED_EMPLOYEE_BORDERS",
                             "ROLE_PERM_BORDERS_SECURITY_COORDINATION_FILTER",
                             "ROLE_PERM_VACATION_REQUEST_SAVE_ALL"
                     ]
                    ],

                    /**
                     * vacation request -> create new list vacation request
                     */
                    [groupName       : "GROUP_VACATION_REQUEST_CREATE_NEW_LIST_VACATION_REQUEST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationRequest.entities', null, 'vacation request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                             "ROLE_PERM_VACATION_REQUEST_SELECTED_EMPLOYEE_BORDERS",
                             "ROLE_PERM_BORDERS_SECURITY_COORDINATION_FILTER",
                             "ROLE_PERM_VACATION_REQUEST_SAVE_ALL"
                     ]
                    ],

                    /**
                     * vacation list ->  vacation list
                     */
                    [groupName       : "GROUP_VACATION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationList.entities', null, 'vacation list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_VACATION_LIST_MANAGE_VACATION_LIST",
                             "ROLE_PERM_VACATION_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_LIST_ADD_VACATION_REQUESTS_MODAL",
                             "ROLE_PERM_VACATION_REQUEST_FILTER",
                             "ROLE_PERM_VACATION_LIST_ADD_VACATION_REQUESTS",
                             "ROLE_PERM_VACATION_LIST_SEND_DATA_MODAL",
                             "ROLE_PERM_VACATION_LIST_SEND_DATA",
                             "ROLE_PERM_VACATION_LIST_RECEIVE_DATA_MODAL",
                             "ROLE_PERM_VACATION_LIST_RECEIVE_DATA",
                             "ROLE_PERM_VACATION_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_VACATION_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_LIST_REJECT_REQUEST",
                             "ROLE_PERM_VACATION_LIST_CLOSE_MODAL",
                             "ROLE_PERM_VACATION_LIST_CLOSE_LIST",
                             "ROLE_PERM_VACATION_LIST_NOTE_LIST",
                             "ROLE_PERM_VACATION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_VACATION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_VACATION_LIST_NOTE_CREATE",
                             "ROLE_PERM_VACATION_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * vacation list ->  vacation create
                     */
                    [groupName       : "GROUP_VACATION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationList.entities', null, 'vacation list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_VACATION_LIST_MANAGE_VACATION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    /**
                     * vacation list ->  vacation update
                     */
                    [groupName       : "GROUP_VACATION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationList.entities', null, 'vacation list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_VACATION_LIST_MANAGE_VACATION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * department -> show
                     */
                    [groupName       : "GROUP_DEPARTMENT_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('department.entities', null, 'department', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm",
                     permissionNames : [
                             "ROLE_PERM_PCORE_CONTACT_METHOD_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_CONTACT_TYPE_AUTO_COMPLETE",
                     ]
                    ],

                    /**
                     * allowance type ->  create
                     */
                    [groupName       : "GROUP_ALLOWANCE_TYPE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceType.entities', null, 'allowance type', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance.lookups",
                     permissionNames : [
                             "ROLE_PERM_RELATIONSHIP_TYPE_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * allowance type ->  list
                     */
                    [groupName       : "GROUP_ALLOWANCE_TYPE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceType.entities', null, 'allowance type', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance.lookups",
                     permissionNames : [
                             "ROLE_PERM_RELATIONSHIP_TYPE_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * allowance type ->  update
                     */
                    [groupName       : "GROUP_ALLOWANCE_TYPE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceType.entities', null, 'allowance type', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance.lookups",
                     permissionNames : [
                             "ROLE_PERM_RELATIONSHIP_TYPE_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * allowance list ->  allowance create
                     */
                    [groupName       : "GROUP_ALLOWANCE_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceList.entities', null, 'allowance list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_ALLOWANCE_LIST_MANAGE_ALLOWANCE_LIST",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_LIST_ADD_ALLOWANCE_REQUESTS_MODAL",
                             "ROLE_PERM_ALLOWANCE_REQUEST_FILTER",
                             "ROLE_PERM_ALLOWANCE_LIST_ADD_ALLOWANCE_REQUESTS",
                             "ROLE_PERM_ALLOWANCE_LIST_SEND_DATA_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_SEND_DATA",
                             "ROLE_PERM_ALLOWANCE_LIST_RECEIVE_DATA_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_RECEIVE_DATA",
                             "ROLE_PERM_ALLOWANCE_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_ALLOWANCE_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_REJECT_REQUEST",
                             "ROLE_PERM_ALLOWANCE_LIST_CLOSE_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_CLOSE_LIST",
                             "ROLE_PERM_ALLOWANCE_LIST_NOTE_LIST",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_ALLOWANCE_LIST_NOTE_CREATE",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * allowance list ->  allowance create
                     */
                    [groupName       : "GROUP_ALLOWANCE_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceList.entities', null, 'allowance list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_ALLOWANCE_LIST_MANAGE_ALLOWANCE_LIST",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_LIST_ADD_ALLOWANCE_REQUESTS_MODAL",
                             "ROLE_PERM_ALLOWANCE_REQUEST_FILTER",
                             "ROLE_PERM_ALLOWANCE_LIST_ADD_ALLOWANCE_REQUESTS",
                             "ROLE_PERM_ALLOWANCE_LIST_SEND_DATA_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_SEND_DATA",
                             "ROLE_PERM_ALLOWANCE_LIST_RECEIVE_DATA_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_RECEIVE_DATA",
                             "ROLE_PERM_ALLOWANCE_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_ALLOWANCE_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_REJECT_REQUEST",
                             "ROLE_PERM_ALLOWANCE_LIST_CLOSE_MODAL",
                             "ROLE_PERM_ALLOWANCE_LIST_CLOSE_LIST",
                             "ROLE_PERM_ALLOWANCE_LIST_NOTE_LIST",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_ALLOWANCE_LIST_NOTE_CREATE",
                             "ROLE_PERM_ALLOWANCE_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],


                    [groupName       : "GROUP_ALLOWANCE_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceList.entity', null, 'allowance list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    [groupName       : "GROUP_ALLOWANCE_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceList.entity', null, 'allowance list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * bordersSecurityCoordination  -> list
                     */
                    [groupName       : "GROUP_BORDERS_SECURITY_COORDINATION_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('bordersSecurityCoordination.entities', null, 'bordersSecurityCoordination list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_BORDER_CROSSING_POINT_AUTOCOMPLETE",
                             "ROLE_PERM_DOCUMENT_TYPE_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * bordersSecurityCoordination  -> create
                     */
                    [groupName       : "GROUP_BORDERS_SECURITY_COORDINATION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('bordersSecurityCoordination.entities', null, 'bordersSecurityCoordination create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_BORDERS_SECURITY_COORDINATION_SELECT_EMPLOYEE",
                             "ROLE_PERM_BORDERS_SECURITY_COORDINATION_CREATE_NEW_BORDERS_SECURITY_COORDINATION",
                     ]
                    ],

                    /**
                     * bordersSecurityCoordination  -> create new borders security coordination
                     */
                    [groupName       : "GROUP_BORDERS_SECURITY_COORDINATION_CREATE_NEW_BORDERS_SECURITY_COORDINATION",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('bordersSecurityCoordination.entities', null, 'bordersSecurityCoordination create new borders security coordination', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [

                             "ROLE_PERM_DOCUMENT_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_BORDER_CROSSING_POINT_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE"
                     ]
                    ],

                    /**
                     * bordersSecurityCoordination  -> update
                     */
                    [groupName       : "GROUP_BORDERS_SECURITY_COORDINATION_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('bordersSecurityCoordination.entities', null, 'bordersSecurityCoordination edit', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [
                             "ROLE_PERM_DOCUMENT_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_BORDER_CROSSING_POINT_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * bordersSecurityCoordination  -> show
                     */
                    [groupName       : "GROUP_BORDERS_SECURITY_COORDINATION_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('bordersSecurityCoordination.entities', null, 'bordersSecurityCoordination show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.request",
                     permissionNames : [
                             "ROLE_PERM_DOCUMENT_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_BORDER_CROSSING_POINT_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * allowance request -> list
                     */
                    [groupName       : "GROUP_ALLOWANCE_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceRequest.entities', null, 'allowance request list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ALLOWANCE_REQUEST_SHOW_THREAD",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT"
                     ]
                    ],

                    /**
                     * allowance request -> create
                     */
                    [groupName       : "GROUP_ALLOWANCE_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceRequest.entities', null, 'allowance request create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_REQUEST_GET_EMPLOYEE",
                             "ROLE_PERM_ALLOWANCE_REQUEST_CREATE_NEW_ALLOWANCE_REQUEST",
                             "ROLE_PERM_PERSON_RELATION_SHIPS_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_STOP_REASON_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_REQUEST_FILTER_CAN_HAVE_OPERATION"
                     ]
                    ],

                    /**
                     * allowance request -> extend
                     */
                    [groupName       : "GROUP_ALLOWANCE_REQUEST_CONTINUE_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceRequest.entities', null, 'allowance request create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_ALLOWANCE_REQUEST_CONTINUE_REQUEST_CREATE",
                             "ROLE_PERM_PERSON_RELATION_SHIPS_AUTOCOMPLETE",
                             "ROLE_PERM_ALLOWANCE_REQUEST_FILTER_CAN_HAVE_OPERATION"
                     ]
                    ],

                    /**
                     * allowance request -> show
                     */
                    [groupName       : "GROUP_ALLOWANCE_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceRequest.entities', null, 'allowance request show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_ALLOWANCE_REQUEST_GO_TO_LIST"
                     ]
                    ],

                    /**
                     * allowance request -> update
                     */
                    [groupName       : "GROUP_ALLOWANCE_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('allowanceRequest.entities', null, 'allowance request edit', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.allowance",
                     permissionNames : [
                             "ROLE_PERM_PERSON_RELATION_SHIPS_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * UpdateMilitaryRankRequest -> list
                     */
                    [groupName       : "GROUP_UPDATE_MILITARY_RANK_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('updateMilitaryRankRequest.entities', null, 'UpdateMilitaryRankRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_CLASSIFICATION_AUTOCOMPLETE",
                             "ROLE_PERM_UPDATE_MILITARY_RANK_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_PROMOTION_LIST_MANAGE_PROMOTION_LIST",
                     ]
                    ],

                    /**
                     * UpdateMilitaryRankRequest -> create
                     */
                    [groupName       : "GROUP_UPDATE_MILITARY_RANK_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('updateMilitaryRankRequest.entities', null, 'UpdateMilitaryRankRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_UPDATE_MILITARY_RANK_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_UPDATE_MILITARY_RANK_REQUEST_CREATE_NEW_REQUEST",
                             "ROLE_PERM_MILITARY_RANK_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_CLASSIFICATION_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * UpdateMilitaryRankRequest -> update request
                     */
                    [groupName       : "GROUP_UPDATE_MILITARY_RANK_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('updateMilitaryRankRequest.entities', null, 'UpdateMilitaryRankRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_MILITARY_RANK_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_CLASSIFICATION_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * UpdateMilitaryRankRequest -> show
                     */
                    [groupName       : "GROUP_UPDATE_MILITARY_RANK_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('updateMilitaryRankRequest.entities', null, 'UpdateMilitaryRankRequest show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_UPDATE_MILITARY_RANK_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_PROMOTION_LIST_MANAGE_PROMOTION_LIST"
                     ]
                    ],

                    /**
                     * PromotionRequest -> list
                     */
                    [groupName       : "GROUP_PROMOTION_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('promotionRequest.entities', null, 'UpdateMilitaryRankRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_DEGREE_AUTOCOMPLETE",
                             "ROLE_PERM_PROMOTION_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_PROMOTION_LIST_MANAGE_PROMOTION_LIST",
                     ]
                    ],

                    /**
                     * PromotionRequest -> create
                     */
                    [groupName       : "GROUP_PROMOTION_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('promotionRequest.entities', null, 'UpdateMilitaryRankRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_PROMOTION_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_PROMOTION_REQUEST_CREATE_NEW_REQUEST",
                             "ROLE_PERM_EDUCATION_DEGREE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * PromotionRequest -> update request
                     */
                    [groupName       : "GROUP_PROMOTION_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('promotionRequest.entities', null, 'UpdateMilitaryRankRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_EDUCATION_DEGREE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * PromotionRequest -> show
                     */
                    [groupName       : "GROUP_PROMOTION_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('promotionRequest.entities', null, 'UpdateMilitaryRankRequest show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_PROMOTION_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_PROMOTION_LIST_MANAGE_PROMOTION_LIST"
                     ]
                    ],

                    /**
                     * promotion list ->  promotion create
                     */
                    [groupName       : "GROUP_PROMOTION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('promotionList.entities', null, 'promotion list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_PROMOTION_LIST_MANAGE_PROMOTION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * promotion list ->  promotion update
                     */
                    [groupName       : "GROUP_PROMOTION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('promotionList.entities', null, 'promotion list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_PROMOTION_LIST_MANAGE_PROMOTION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * promotion list -> manage promotion list
                     */
                    [groupName       : "GROUP_PROMOTION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('promotionList.entities', null, 'promotion list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.promotion",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_CLASSIFICATION_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_FILTER_EMPLOYEE_FOR_MODAL",
                             "ROLE_PERM_REQUEST_FILTER_PROMOTION_REQUEST",
                             "ROLE_PERM_PROMOTION_LIST_MANAGE_PROMOTION_LIST",
                             "ROLE_PERM_PROMOTION_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_ADD_ELIGIBLE_EMPLOYEE_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_ADD_EXCEPTION_EMPLOYEE_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_PROMOTION_LIST_SEND_LIST",
                             "ROLE_PERM_PROMOTION_LIST_RECEIVE_LIST",
                             "ROLE_PERM_PROMOTION_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_PROMOTION_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_PROMOTION_LIST_CLOSE_LIST",
                             "ROLE_PERM_PROMOTION_LIST_ADD_PROMOTION_REQUEST_TO_LIST",
                             "ROLE_PERM_PROMOTION_LIST_ADD_EMPLOYEE_TO_LIST",
                             "ROLE_PERM_PROMOTION_LIST_NOTE_LIST",
                             "ROLE_PERM_PROMOTION_LIST_NOTE_CREATE",
                             "ROLE_PERM_PROMOTION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_PROMOTION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_PROMOTION_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],
                    [groupName       : "GROUP_VACANCY_ADVERTISEMENTS_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacancyAdvertisements.entities', null, 'vacancy advertisement list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_VACANCY_ADVERTISEMENTS_GET_VACANCIES",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_VACANCY_ADVERTISEMENTS_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacancyAdvertisements.entities', null, 'vacancy advertisement list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_VACANCY_ADVERTISEMENTS_GET_VACANCIES",
                             "ROLE_PERM_JOB_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_VACANCY_ADVERTISEMENTS_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacancyAdvertisements.entities', null, 'vacancy advertisement list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_TABS_LOAD_TAB",
                     ]
                    ],

                    /**
                     * vacancyAdvertisementsVacancy tab -> list
                     */
                    [groupName       : "GROUP_VACANCY_ADVERTISEMENTS_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacancyAdvertisements.entities', null, 'vacancy advertisement list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_VACANCY_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_JOINED_VACANCY_ADVERTISEMENT_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('joinedVacancyAdvertisement.entities', null, 'vacancy advertisement list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_VACANCY_ADVERTISEMENTS_DELETE_VACANCY_FROM_VACANCY_ADVERTISEMENTS",
                             "ROLE_PERM_VACANCY_ADVERTISEMENTS_ADD_VACANCY_TO_VACANCY_ADVERTISEMENT",
                     ]
                    ],

                    /**
                     * interview -> list
                     */
                    [groupName       : "GROUP_INTERVIEW_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('interview.entities', null, 'interview list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_VACANCY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_LOCATION_AUTO_COMPLETE",
                             "ROLE_PERM_INTERVIEW_CHANGE_INTERVIEW_STATUS"
                     ]
                    ],

                    /**
                     * interview -> create
                     */
                    [groupName       : "GROUP_INTERVIEW_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('interview.entities', null, 'interview create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_VACANCY_AUTOCOMPLETE",
                             "ROLE_PERM_CONTACT_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_CONTACT_METHOD_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                             "ROLE_PERM_COMMITTE_ROLE_AUTOCOMPLETE",
                             "ROLE_PERM_APPLICANT_GET_VACANCIES"
                     ]
                    ],

                    /**
                     * interview -> update
                     */
                    [groupName       : "GROUP_INTERVIEW_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('interview.entities', null, 'interview edit', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_RECRUITMENT_CYCLE_AUTOCOMPLETE",
                             "ROLE_PERM_VACANCY_AUTOCOMPLETE",
                             "ROLE_PERM_CONTACT_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_CONTACT_METHOD_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                             "ROLE_PERM_COMMITTE_ROLE_AUTOCOMPLETE",
                             "ROLE_PERM_APPLICANT_GET_VACANCIES"
                     ]
                    ],

                    /**
                     * interview -> show -> applicant tab
                     */
                    [groupName       : "GROUP_INTERVIEW_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicant.entities', null, 'interview applicant tab', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_TABS_LOAD_TAB",
                             "ROLE_PERM_APPLICANT_FILTER",
                             "ROLE_PERM_VACANCY_AUTOCOMPLETE",
                             "ROLE_PERM_INTERVIEW_ADD_APPLICANT_TO_INTERVIEW",
                             "ROLE_PERM_INTERVIEW_DELETE_APPLICANT_FROM_INTERVIEW",
                             "ROLE_PERM_INTERVIEW_GET_APPLICANTS"
                     ]
                    ],

                    /**
                     * inspectionCategory -> list
                     */
                    [groupName       : "GROUP_INSPECTION_CATEGORY_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('inspectionCategory.entities', null, 'inspectionCategory list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_COMMITTEE_ROLE_AUTOCOMPLETE",

                     ]
                    ],

                    /**
                     * inspection -> list
                     */
                    [groupName       : "GROUP_INSPECTION_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('inspectionCategory.entities', null, 'inspection list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_COMMITTEE_ROLE_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * inspection -> create
                     */
                    [groupName       : "GROUP_INSPECTION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('inspectionCategory.entities', null, 'inspection create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * inspection -> edit
                     */
                    [groupName       : "GROUP_INSPECTION_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('inspectionCategory.entities', null, 'inspection update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * job tittle -> list
                     */
                    [groupName       : "GROUP_JOB_TITLE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobTitle.entities', null, 'jobTitle list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * job tittle -> create
                     */
                    [groupName       : "GROUP_JOB_TITLE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobTitle.entities', null, 'jobTitle create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_JOB_REQUIREMENT_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * job tittle -> update
                     */
                    [groupName       : "GROUP_JOB_TITLE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobTitle.entities', null, 'jobTitle update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_JOB_REQUIREMENT_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * job requirement -> list
                     */
                    [groupName       : "GROUP_JOB_REQUIREMENT_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequirement.entities', null, 'jobRequirement list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_TITTLE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * job requirement -> create
                     */
                    [groupName       : "GROUP_JOB_REQUIREMENT_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequirement.entities', null, 'jobRequirement create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_TITTLE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * job requirement -> update
                     */
                    [groupName       : "GROUP_JOB_REQUIREMENT_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('jobRequirement.entities', null, 'jobRequirement edit', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_TITTLE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * job  -> list
                     */
                    [groupName       : "GROUP_JOB_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('job.entities', null, 'job list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * job  -> create
                     */
                    [groupName       : "GROUP_JOB_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('job.entities', null, 'job create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_OPERATIONAL_TASK_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * job  -> update
                     */
                    [groupName       : "GROUP_JOB_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('job.entities', null, 'job update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_DEGREE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_EDUCATION_MAJOR_AUTO_COMPLETE",
                             "ROLE_PERM_OPERATIONAL_TASK_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * employee status  -> list
                     */
                    [groupName       : "GROUP_EMPLOYEE_STATUS_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeStatus.entities', null, 'employee status list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_JOB_CATEGORY_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * disciplinaryListJudgmentSetup  -> list
                     */
                    [groupName       : "GROUP_DISCIPLINARY_LIST_JUDGMENT_SETUP_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryListJudgmentSetup.entities', null, 'disciplinaryListJudgmentSetup list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_JUDGMENT_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * disciplinaryListJudgmentSetup  -> create
                     */
                    [groupName       : "GROUP_DISCIPLINARY_LIST_JUDGMENT_SETUP_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryListJudgmentSetup.entities', null, 'disciplinaryListJudgmentSetup create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_JUDGMENT_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * disciplinaryListJudgmentSetup  -> update
                     */
                    [groupName       : "GROUP_DISCIPLINARY_LIST_JUDGMENT_SETUP_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryListJudgmentSetup.entities', null, 'disciplinaryListJudgmentSetup update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_JUDGMENT_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * vacationType  -> list
                     */
                    [groupName       : "GROUP_VACATION_TYPE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationType.entities', null, 'vacationType list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_COLOR_AUTO_COMPLETE"
                     ]
                    ],
                    /**
                     * vacationType  -> create
                     */
                    [groupName       : "GROUP_VACATION_TYPE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationType.entities', null, 'vacationType create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_COLOR_AUTO_COMPLETE"
                     ]
                    ],

                    /**
                     * vacationType  -> update
                     */
                    [groupName       : "GROUP_VACATION_TYPE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationType.entities', null, 'vacationType update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_COLOR_AUTO_COMPLETE"
                     ]
                    ],

                    /**
                     * vacationConfiguration  -> list
                     */
                    [groupName       : "GROUP_VACATION_CONFIGURATION_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationConfiguration.entities', null, 'vacationConfiguration list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_VACATION_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",

                     ]
                    ],

                    /**
                     * vacationConfiguration  -> create
                     */
                    [groupName       : "GROUP_VACATION_CONFIGURATION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationConfiguration.entities', null, 'vacationConfiguration create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_RELIGION_AUTO_COMPLETE"

                     ]
                    ],

                    /**
                     * vacationConfiguration  -> update
                     */
                    [groupName       : "GROUP_VACATION_CONFIGURATION_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationConfiguration.entities', null, 'vacationConfiguration update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_RELIGION_AUTO_COMPLETE"

                     ]
                    ],

                    /**
                     * employeeVacationBalance  -> list
                     */
                    [groupName       : "GROUP_EMPLOYEE_VACATION_BALANCE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeVacationBalance.entities', null, 'employeeVacationBalance list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_CONFIGURATION_AUTOCOMPLETE",


                     ]
                    ],

                    /**
                     * employeeVacationBalance  -> create
                     */
                    [groupName       : "GROUP_EMPLOYEE_VACATION_BALANCE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeVacationBalance.entities', null, 'employeeVacationBalance create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_VACATION_BALANCE_FILTER",
                             "ROLE_PERM_EMPLOYEE_VACATION_BALANCE_CALCULATE_EMPLOYEE_YEARLY_BALANCE",
                             "ROLE_PERM_EMPLOYEE_VACATION_BALANCE_CALCULATE_ALL_EMPLOYEE_YEARLY_BALANCE"

                     ]
                    ],

                    /**
                     * employeeVacationBalance  -> showEmployeeBalance
                     */
                    [groupName       : "GROUP_EMPLOYEE_VACATION_BALANCE_SHOW_EMPLOYEE_BALANCE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeVacationBalance.entities', null, 'employeeVacationBalance showEmployeeBalance', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_VACATION_BALANCE_FILTER",
                     ]
                    ],

                    /**
                     * firm  -> list
                     */
                    [groupName       : "GROUP_FIRM_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('firm.entities', null, 'firm list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                     ]
                    ],

                    /**
                     * firm  -> create
                     */
                    [groupName       : "GROUP_FIRM_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('firm.entities', null, 'firm create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.lookups",
                     permissionNames : [
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_CORPORATION_CLASSIFICATION_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_ACTIVITY_AUTO_COMPLETE",
                             "ROLE_PERM_ORGANIZATION_TYPE_AUTO_COMPLETE",
                             "ROLE_PERM_WORKING_SECTOR_AUTO_COMPLETE",
                             "ROLE_PERM_FIRM_SAVE_ORGANIZATION"
                     ]
                    ],

                    //loan request
                    [groupName       : "GROUP_LOAN_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_LOAN_REQUEST_GO_TO_LIST",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entity', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entity', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entity', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_LOAN_REQUEST_GO_TO_LIST",
                     ]
                    ],

                    //loan list
                    [groupName       : "GROUP_LOAN_LIST_PERMISSION_MANAGEMENT",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanList.label', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_LOAN_REQUEST_ADD_TRANSFER",
                             "ROLE_PERM_LOAN_REQUEST_SAVE_TRANSFER",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_LOAN_LIST_MANAGE_LOAN_LIST",
                             "ROLE_PERM_LOAN_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_LOAN_LIST_ADD_REQUEST",
                             "ROLE_PERM_LOAN_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_LOAN_LIST_SEND_LIST",
                             "ROLE_PERM_LOAN_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_LOAN_LIST_RECEIVE_LIST",
                             "ROLE_PERM_LOAN_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_LOAN_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_LOAN_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_LOAN_LIST_REJECT_REQUEST",
                             "ROLE_PERM_LOAN_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_LOAN_LIST_CLOSE_LIST",
                             "ROLE_PERM_LOAN_LIST_NOTE_LIST",
                             "ROLE_PERM_LOAN_LIST_NOTE_CREATE",
                             "ROLE_PERM_LOAN_LIST_GET_RECEIVED_LOAN_PERSON_AJAX",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_GOVERNORATE_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    //loan notice
                    [groupName       : "GROUP_LOAN_NOTICE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entities', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_LOAN_NOTICE_END_NOMINATION",
                             "ROLE_PERM_LOAN_NOTICE_CLOSE_NOMINATION",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_NOTICE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entity', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_NOTICE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entity', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_NOTICE_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanRequest.entity', null, 'Tabs', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_LOAN_NOTICE_END_NOMINATION",
                             "ROLE_PERM_LOAN_NOTICE_CLOSE_NOMINATION",
                     ]
                    ],

                    //loan notice replay request
                    [groupName       : "GROUP_LOAN_NOTICE_REPLAY_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanNoticeReplayRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_CREATE_NEW_EXTERNAL_TRANSFER_REQUEST",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROUP_LOAN_NOTICE_REPLAY_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanNoticeReplayRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],

                    [groupName       : "GROU_LOAN_NOTICE_REPLAY_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanNoticeReplayRequest.entities', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_GO_TO_LIST",
                     ]
                    ],

                    [groupName       : "GROUP_LOAN_NOTICE_REPLAY_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanNoticeReplayRequest.entity', null, 'disciplinary request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_GO_TO_LIST",
                     ]
                    ],

                    //loan notice replay list
                    [groupName       : "GROUP_LOAN_NOTICE_REPLAY_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('loanNoticeReplayList.entities', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.loan",
                     permissionNames : [
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_MANAGE_LOAN_NOTICE_REPLAY_LIST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_ADD_REQUEST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_SEND_LIST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_RECEIVE_LIST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_REJECT_REQUEST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_CLOSE_LIST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_NOTE_LIST",
                             "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_NOTE_CREATE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_GOVERNORATE_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_NOTICE_REPLAY_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryList.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],
                    [groupName       : "GROUP_LOAN_NOTICE_REPLAY_LIST_EDIT",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('disciplinaryList.entity', null, 'disciplinary list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * employmentServiceRequest -> list
                     */
                    [groupName       : "GROUP_EMPLOYMENT_SERVICE_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('endOfService.entities', null, 'endOfService request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.employmentService",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_SERVICE_ACTION_REASON_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_SERVICE_LIST_MANAGE_SERVICE_LIST",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_LIST_END_OF_SERVICE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_LIST_RETURN_TO_SERVICE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_LIST",
                     ]
                    ],

                    /**
                     * employmentServiceRequest -> create
                     */
                    [groupName       : "GROUP_EMPLOYMENT_SERVICE_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('endOfService.entities', null, 'endOfService request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.employmentService",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_SELECT_EMPLOYEE_END_OF_SERVICE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_CREATE_END_OF_SERVICE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_REDIRECT_END_OF_SERVICE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_SELECT_EMPLOYEE_RETURN_TO_SERVICE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_CREATE_RETURN_TO_SERVICE",
                             "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_REDIRECT_RETURN_TO_SERVICE",
                             "ROLE_PERM_SERVICE_ACTION_REASON_AUTOCOMPLETE",
                             "ROLE_PERM_SERVICE_ACTION_REASON_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * employmentServiceRequest -> update
                     */
                    [groupName       : "GROUP_EMPLOYMENT_SERVICE_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('endOfService.entities', null, 'endOfService request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.employmentService",
                     permissionNames : [
                             "ROLE_PERM_SERVICE_ACTION_REASON_AUTOCOMPLETE",
                             "ROLE_PERM_SERVICE_ACTION_REASON_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * employmentServiceRequest -> show
                     */
                    [groupName       : "GROUP_EMPLOYMENT_SERVICE_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('endOfService.entities', null, 'endOfService show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.employmentService",
                     permissionNames : [
                             "ROLE_PERaM_EMPLOYMENT_SERVICE_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_SERVICE_LIST_MANAGE_SERVICE_LIST",
                     ]
                    ],

                    /**
                     * service list -> create
                     */
                    [groupName       : "GROUP_SERVICE_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('serviceList.entities', null, 'promotion list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.employmentService",
                     permissionNames : [
                             "ROLE_PERM_SERVICE_LIST_MANAGE_SERVICE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * service list -> update
                     */
                    [groupName       : "GROUP_SERVICE_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('serviceList.entities', null, 'promotion list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.employmentService",
                     permissionNames : [
                             "ROLE_PERM_SERVICE_LIST_MANAGE_SERVICE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * service list -> manage service list
                     */
                    [groupName       : "GROUP_SERVICE_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('serviceList.entities', null, 'promotion list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.employmentService",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_SERVICE_LIST_LIST_END_OF_SERVICE_LIST",
                             "ROLE_PERM_SERVICE_LIST_LIST_RETURN_TO_SERVICE_LIST",
                             "ROLE_PERM_SERVICE_LIST_CREATE_END_OF_SERVICE_LIST",
                             "ROLE_PERM_SERVICE_LIST_CREATE_RETURN_TO_SERVICE_LIST",
                             "ROLE_PERM_SERVICE_LIST_MANAGE_SERVICE_LIST",
                             "ROLE_PERM_SERVICE_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_SERVICE_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_SERVICE_LIST_ADD_EXCEPTION_MODAL",
                             "ROLE_PERM_SERVICE_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_SERVICE_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_SERVICE_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_SERVICE_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_SERVICE_LIST_SEND_LIST",
                             "ROLE_PERM_SERVICE_LIST_RECEIVE_LIST",
                             "ROLE_PERM_SERVICE_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_SERVICE_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_SERVICE_LIST_CLOSE_LIST",
                             "ROLE_PERM_SERVICE_LIST_ADD_EMPLOYMENT_SERVICE_REQUEST_TO_LIST",
                             "ROLE_PERM_SERVICE_LIST_ADD_EXCEPTIONAL_TO_LIST",
                             "ROLE_PERM_SERVICE_LIST_NOTE_LIST",
                             "ROLE_PERM_SERVICE_LIST_NOTE_CREATE",
                             "ROLE_PERM_SERVICE_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_SERVICE_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_SERVICE_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * absence -> list
                     */
                    [groupName       : "GROUP_ABSENCE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('absence.entities', null, 'absence request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_SHOW_RELATED_REQUEST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_CREATE_NEW_REQUEST",
                     ]
                    ],

                    /**
                     * absence -> create
                     */
                    [groupName       : "GROUP_ABSENCE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('absence.entities', null, 'absence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_ABSENCE_SELECT_EMPLOYEE",
                             "ROLE_PERM_ABSENCE_CREATE_NEW_ABSENCE",


                     ]
                    ],

                    /**
                     * absence -> update absence
                     */
                    [groupName       : "GROUP_ABSENCE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('absence.entities', null, 'absence', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * violation list -> create
                     */
                    [groupName       : "GROUP_VIOLATION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('violationList.entities', null, 'absence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_VIOLATION_LIST_MANAGE_VIOLATION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * violation list -> update
                     */
                    [groupName       : "GROUP_VIOLATION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('violationList.entities', null, 'absence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_VIOLATION_LIST_MANAGE_VIOLATION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * violation list -> list
                     */
                    [groupName       : "GROUP_VIOLATION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('violationList.entities', null, 'absence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_AUTOCOMPLETE",
                             "ROLE_PERM_VIOLATION_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_VIOLATION_LIST_MANAGE_VIOLATION_LIST",
                             "ROLE_PERM_VIOLATION_LIST_SEND_LIST",
                             "ROLE_PERM_VIOLATION_LIST_ADD_VIOLATION_MODAL",
                             "ROLE_PERM_VIOLATION_LIST_ADD_VIOLATION_TO_LIST",
                             "ROLE_PERM_EMPLOYEE_VIOLATION_FILTER_VIOLATION_FOR_LIST",
                             "ROLE_PERM_VIOLATION_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_VIOLATION_LIST_CLOSE_LIST",
                             "ROLE_PERM_VIOLATION_LIST_NOTE_LIST",
                             "ROLE_PERM_VIOLATION_LIST_NOTE_CREATE",
                             "ROLE_PERM_VIOLATION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_VIOLATION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_VIOLATION_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * violation list -> addViolationModal
                     */
                    [groupName       : "GROUP_VIOLATION_LIST_ADD_VIOLATION_MODAL",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('violationList.entities', null, 'absence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_VIOLATION_FILTER_VIOLATION_FOR_LIST",
                     ]
                    ],

                    /**
                     * dispatchRequest -> list
                     */
                    [groupName       : "GROUP_DISPATCH_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('dispatchRequest.entities', null, 'dispatchRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.dispatch",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_MAJOR_AUTOCOMPLETE",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_DISPATCH_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_DISPATCH_REQUEST_SHOW_THREAD",
                             "ROLE_PERM_DISPATCH_REQUEST_EXTENSION_REQUEST_LIST",
                             "ROLE_PERM_DISPATCH_REQUEST_EXTENSION_REQUEST_CREATE",
                             "ROLE_PERM_DISPATCH_REQUEST_EXTENSION_REQUEST_EDIT",
                             "ROLE_PERM_DISPATCH_REQUEST_EXTENSION_REQUEST_SHOW",
                             "ROLE_PERM_DISPATCH_REQUEST_STOP_REQUEST_CREATE",
                             "ROLE_PERM_DISPATCH_EXTENSION_REQUEST_LIST",
                             "ROLE_PERM_DISPATCH_EXTENSION_REQUEST_IS_ALLOW_TO_CREATE_EXTENSION",
                             "ROLE_PERM_DISPATCH_STOP_REQUEST_LIST",
                             "ROLE_PERM_DISPATCH_LIST_MANAGE_DISPATCH_LIST",

                     ]
                    ],

                    /**
                     * dispatchRequest -> create
                     */
                    [groupName       : "GROUP_DISPATCH_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('dispatchRequest.entities', null, 'dispatchRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.dispatch",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_DISPATCH_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_DISPATCH_REQUEST_CREATE_NEW_DISPATCH_REQUEST",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_MAJOR_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                     ]
                    ],

                    /**
                     * dispatchRequest -> update
                     */
                    [groupName       : "GROUP_DISPATCH_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('dispatchRequest.entities', null, 'dispatchRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.dispatch",
                     permissionNames : [
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_MAJOR_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_LOCALITY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BLOCK_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_STREET_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_BUILDING_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_AREA_CLASS_AUTO_COMPLETE",
                     ]
                    ],

                    /**
                     * dispatchRequest -> show
                     */
                    [groupName       : "GROUP_DISPATCH_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('dispatchRequest.entities', null, 'dispatchRequest show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.dispatch",
                     permissionNames : [
                             "ROLE_PERM_DISPATCH_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_DISPATCH_EXTENSION_REQUEST_LIST",
                             "ROLE_PERM_DISPATCH_STOP_REQUEST_LIST",
                             "ROLE_PERM_DISPATCH_LIST_MANAGE_DISPATCH_LIST",
                     ]
                    ],

                    /**
                     * dispatch list -> create
                     */
                    [groupName       : "GROUP_DISPATCH_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('dispatchList.entities', null, 'dispatch list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.dispatch",
                     permissionNames : [
                             "ROLE_PERM_DISPATCH_LIST_MANAGE_DISPATCH_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * dispatch list -> update
                     */
                    [groupName       : "GROUP_DISPATCH_LIST_UPGRADE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('dispatchList.entities', null, 'dispatch list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.dispatch",
                     permissionNames : [
                             "ROLE_PERM_DISPATCH_LIST_MANAGE_DISPATCH_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * dispatch list -> manage dispatch list
                     */
                    [groupName       : "GROUP_DISPATCH_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('dispatchList.entities', null, 'dispatch list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.dispatch",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ORGANIZATION_AUTOCOMPLETE",
                             "ROLE_PERM_EDUCATION_MAJOR_AUTOCOMPLETE",
                             "ROLE_PERM_DEPARTMENT_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                             "ROLE_PERM_DISPATCH_LIST_MANAGE_DISPATCH_LIST",
                             "ROLE_PERM_DISPATCH_LIST_ADD_DISPATCH_REQUEST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_ADD_DISPATCH_EXTENSION_REQUEST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_ADD_DISPATCH_STOP_REQUEST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_DISPATCH_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_SEND_LIST",
                             "ROLE_PERM_DISPATCH_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_RECEIVE_LIST",
                             "ROLE_PERM_DISPATCH_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_CLOSE_LIST",
                             "ROLE_PERM_DISPATCH_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_DISPATCH_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_DISPATCH_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_DISPATCH_LIST_NOTE_LIST",
                             "ROLE_PERM_DISPATCH_LIST_NOTE_CREATE",
                             "ROLE_PERM_DISPATCH_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_DISPATCH_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_DISPATCH_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * childRequest -> list
                     */
                    [groupName       : "GROUP_CHILD_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('childRequest.entities', null, 'childRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.child",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_CHILD_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_CHILD_LIST_MANAGE_CHILD_LIST",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_CHILD_LIST_EMPLOYEE_FILTER_REQUEST",
                             "ROLE_PERM_CHILD_REQUEST_SHOW_THREAD",
                     ]
                    ],

                    /**
                     * childRequest -> create
                     */
                    [groupName       : "GROUP_CHILD_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('childRequest.entities', null, 'childRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.child",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_CHILD_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_CHILD_REQUEST_CREATE_NEW_REQUEST",
                             "ROLE_PERM_CHILD_REQUEST_CREATE_NEW_PERSON",
                             "ROLE_PERM_CHILD_REQUEST_GET_PERSON_DETAILS",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_CHILD_REQUEST_SAVE_NEW_PERSON",
                     ]
                    ],

                    /**
                     * childRequest -> update request
                     */
                    [groupName       : "GROUP_CHILD_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('childRequest.entities', null, 'childRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.child",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_CHILD_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_CHILD_REQUEST_CREATE_NEW_REQUEST",
                             "ROLE_PERM_CHILD_REQUEST_CREATE_NEW_PERSON",
                             "ROLE_PERM_CHILD_REQUEST_GET_PERSON_DETAILS",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * childRequest -> show
                     */
                    [groupName       : "GROUP_CHILD_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('childRequest.entities', null, 'childRequest show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.child",
                     permissionNames : [
                             "ROLE_PERM_CHILD_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_CHILD_LIST_MANAGE_CHILD_LIST",
                             "ROLE_PERM_CHILD_LIST_EMPLOYEE_FILTER_REQUEST",
                     ]
                    ],

                    /**
                     * child list -> create
                     */
                    [groupName       : "GROUP_CHILD_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('childList.entities', null, 'child list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.child",
                     permissionNames : [
                             "ROLE_PERM_CHILD_LIST_MANAGE_CHILD_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * child list -> update
                     */
                    [groupName       : "GROUP_CHILD_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('childList.entities', null, 'child list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.child",
                     permissionNames : [
                             "ROLE_PERM_CHILD_LIST_MANAGE_CHILD_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * child list -> manage child list
                     */
                    [groupName       : "GROUP_CHILD_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('childList.entities', null, 'child list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.child",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_CHILD_LIST_MANAGE_CHILD_LIST",
                             "ROLE_PERM_CHILD_LIST_EMPLOYEE_FILTER_REQUEST",
                             "ROLE_PERM_CHILD_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_CHILD_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_CHILD_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_CHILD_LIST_SEND_LIST",
                             "ROLE_PERM_CHILD_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_CHILD_LIST_RECEIVE_LIST",
                             "ROLE_PERM_CHILD_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_CHILD_LIST_CLOSE_LIST",
                             "ROLE_PERM_CHILD_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_CHILD_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_CHILD_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_CHILD_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_CHILD_LIST_NOTE_LIST",
                             "ROLE_PERM_CHILD_LIST_NOTE_CREATE",
                             "ROLE_PERM_CHILD_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_CHILD_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_CHILD_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * maritalStatusRequest -> list
                     */
                    [groupName       : "GROUP_MARITAL_STATUS_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('maritalStatusRequest.entities', null, 'maritalStatusRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_MARITAL_STATUS_LIST_MANAGE_MARITAL_STATUS_LIST",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_SHOW_THREAD",
                             "ROLE_PERM_MARITAL_STATUS_LIST_EMPLOYEE_FILTER_REQUEST",
                     ]
                    ],

                    /**
                     * maritalStatusRequest -> create
                     */
                    [groupName       : "GROUP_MARITAL_STATUS_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('maritalStatusRequest.entities', null, 'maritalStatusRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_CREATE_NEW_REQUEST",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_CREATE_NEW_PERSON",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_GET_PERSON_DETAILS",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_MARITAL_STATUS_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * maritalStatusRequest -> update request
                     */
                    [groupName       : "GROUP_MARITAL_STATUS_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('maritalStatusRequest.entities', null, 'maritalStatusRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_CREATE_NEW_REQUEST",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_CREATE_NEW_PERSON",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_GET_PERSON_DETAILS",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_PERSON_AUTOCOMPLETE"
                     ]
                    ],

                    /**
                     * maritalStatusRequest -> show
                     */
                    [groupName       : "GROUP_MARITAL_STATUS_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('maritalStatusRequest.entities', null, 'maritalStatusRequest show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_MARITAL_STATUS_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_EMPLOYEE_FILTER_REQUEST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_MANAGE_MARITAL_STATUS_LIST"
                     ]
                    ],

                    /**
                     * maritalStatus list -> create
                     */
                    [groupName       : "GROUP_MARITAL_STATUS_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('maritalStatusList.entities', null, 'maritalStatusList list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_MARITAL_STATUS_LIST_MANAGE_MARITAL_STATUS_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * maritalStatus list -> update
                     */
                    [groupName       : "GROUP_MARITAL_STATUS_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('maritalStatusList.entities', null, 'maritalStatusList list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_MARITAL_STATUS_LIST_MANAGE_MARITAL_STATUS_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * maritalStatus list -> manage maritalStatus list
                     */
                    [groupName       : "GROUP_MARITAL_STATUS_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('maritalStatusList.entities', null, 'maritalStatusList list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.maritalStatus",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_MARITAL_STATUS_AUTO_COMPLETE",
                             "ROLE_PERM_MARITAL_STATUS_LIST_MANAGE_MARITAL_STATUS_LIST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_EMPLOYEE_FILTER_REQUEST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_MARITAL_STATUS_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_MARITAL_STATUS_LIST_SEND_LIST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_MARITAL_STATUS_LIST_RECEIVE_LIST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_MARITAL_STATUS_LIST_CLOSE_LIST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_MARITAL_STATUS_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_MARITAL_STATUS_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_MARITAL_STATUS_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_MARITAL_STATUS_LIST_NOTE_LIST",
                             "ROLE_PERM_MARITAL_STATUS_LIST_NOTE_CREATE",
                             "ROLE_PERM_MARITAL_STATUS_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_MARITAL_STATUS_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_MARITAL_STATUS_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * for custom role : role super admin to set employee profile
                     */
                    [groupName       : "GROUP_HR_SUPER_ADMIN_EMPLOYEE_STATUS",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeStatusHistory.manager.label', null, 'manage employee status', arabicLocal),
                     groupCagtegory  : "hrManagerGroup",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_STATUS_FILTER",
                             "ROLE_PERM_EMPLOYEE_STATUS_EDIT",
                             "ROLE_PERM_EMPLOYEE_STATUS_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_STATUS_SHOW",
                             "ROLE_PERM_EMPLOYEE_STATUS_SAVE",
                             "ROLE_PERM_EMPLOYEE_STATUS_INDEX",
                             "ROLE_PERM_EMPLOYEE_STATUS_UPDATE",
                             "ROLE_PERM_EMPLOYEE_STATUS_CREATE",
                             "ROLE_PERM_EMPLOYEE_STATUS_LIST",
                             "ROLE_PERM_EMPLOYEE_STATUS_DELETE",
                     ]
                    ],
                    [groupName       : "GROUP_EMPLOYMENT_RECORD_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employmentRecord.entities', null, 'employment record', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profile",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYMENT_RECORD_SHOW_INTERNAL_ASSIGNATION",
                     ]
                    ],
                    [groupName       : "GROUP_STRUCTURE_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('structure.entities', null, 'structure', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.common",
                     permissionNames : [
                             "ROLE_PERM_STRUCTURE_GET_DEPARTMENT_INFO",
                     ]
                    ],

                    /**
                     * suspension extension list ->   list
                     */
                    [groupName       : "GROUP_SUSPENSION_EXTENSION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionExtensionList.entities', null, 'suspension list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_EXTENSION_REQUEST_FILTER",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_MANAGE_LIST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_SEND_LIST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_RECEIVE_LIST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_REJECT_REQUEST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_CLOSE_MODAL",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_CLOSE_LIST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_NOTE_LIST",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_NOTE_CREATE",
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * suspension extension list ->   create
                     */
                    [groupName       : "GROUP_SUSPENSION_EXTENSION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionExtensionList.entities', null, 'suspension extension list create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_MANAGE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * vacation extension list ->   create
                     */
                    [groupName       : "GROUP_VACATION_EXTENSION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationExtensionList.entities', null, 'vacation extension list create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_VACATION_EXTENSION_LIST_MANAGE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * vacation stop list ->   create
                     */
                    [groupName       : "GROUP_VACATION_STOP_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationStopList.entities', null, 'vacation stop list create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_VACATION_STOP_LIST_MANAGE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * suspension extension list ->   update
                     */
                    [groupName       : "GROUP_SUSPENSION_EXTENSION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('suspensionExtensionList.entities', null, 'suspension extension list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.suspension",
                     permissionNames : [
                             "ROLE_PERM_SUSPENSION_EXTENSION_LIST_MANAGE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * vacation stop list ->   list
                     */
                    [groupName       : "GROUP_VACATION_STOP_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationStopList.entities', null, 'vacation stop list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_STOP_VACATION_REQUEST_FILTER",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_VACATION_STOP_LIST_MANAGE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_STOP_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_SEND_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_RECEIVE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_VACATION_STOP_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_REJECT_REQUEST",
                             "ROLE_PERM_VACATION_STOP_LIST_CLOSE_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_CLOSE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_NOTE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_VACATION_STOP_LIST_NOTE_CREATE",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * vacation stop list ->   update
                     */
                    [groupName       : "GROUP_VACATION_STOP_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationStopList.entities', null, 'vacation stop list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_STOP_VACATION_REQUEST_FILTER",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_VACATION_STOP_LIST_MANAGE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_STOP_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_SEND_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_RECEIVE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_VACATION_STOP_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_REJECT_REQUEST",
                             "ROLE_PERM_VACATION_STOP_LIST_CLOSE_MODAL",
                             "ROLE_PERM_VACATION_STOP_LIST_CLOSE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_NOTE_LIST",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_VACATION_STOP_LIST_NOTE_CREATE",
                             "ROLE_PERM_VACATION_STOP_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * suspension extension list ->   update
                     */
                    [groupName       : "GROUP_SUSPENSION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationStopList.entities', null, 'vacation stop list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_VACATION_STOP_LIST_MANAGE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * vacation extension list ->   list
                     */
                    [groupName       : "GROUP_VACATION_EXTENSION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('vacationExtensionList.entities', null, 'vacation extension list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.vacation",
                     permissionNames : [
                             "ROLE_PERM_VACATION_EXTENSION_REQUEST_FILTER",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_MANAGE_LIST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_TYPE_AUTOCOMPLETE",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_SEND_LIST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_RECEIVE_LIST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_REJECT_REQUEST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_CLOSE_MODAL",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_CLOSE_LIST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_NOTE_LIST",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_NOTE_CREATE",
                             "ROLE_PERM_VACATION_EXTENSION_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * petitionRequest -> list
                     */
                    [groupName       : "GROUP_PETITION_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('petitionRequest.entities', null, 'petitionRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_SHOW",
                             "ROLE_PERM_PETITION_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_PETITION_LIST_MANAGE_PETITION_LIST",
                             "ROLE_PERM_PETITION_LIST_EMPLOYEE_FILTER_REQUEST",
                     ]
                    ],

                    /**
                     * petitionRequest -> create
                     */
                    [groupName       : "GROUP_PETITION_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('petitionRequest.entities', null, 'petitionRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_REQUEST_AUTOCOMPLETE",
                             "ROLE_PERM_PETITION_REQUEST_SELECT_REQUEST",
                             "ROLE_PERM_PETITION_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_PETITION_REQUEST_CREATE_NEW_REQUEST",
                     ]
                    ],

                    /**
                     * petitionRequest -> update request
                     */
                    [groupName       : "GROUP_PETITION_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('petitionRequest.entities', null, 'petitionRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_DISCIPLINARY_REQUEST_AUTOCOMPLETE",
                             "ROLE_PERM_PETITION_REQUEST_SELECT_REQUEST",
                             "ROLE_PERM_PETITION_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_PETITION_REQUEST_CREATE_NEW_REQUEST",
                     ]
                    ],

                    /**
                     * petitionRequest -> show
                     */
                    [groupName       : "GROUP_PETITION_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('petitionRequest.entities', null, 'petitionRequest show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_PETITION_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_PETITION_LIST_MANAGE_PETITION_LIST",
                             "ROLE_PERM_PETITION_LIST_EMPLOYEE_FILTER_REQUEST",
                     ]
                    ],

                    /**
                     * petition list -> create
                     */
                    [groupName       : "GROUP_PETITION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('petitionList.entities', null, 'petition list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_PETITION_LIST_MANAGE_PETITION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * petition list -> update
                     */
                    [groupName       : "GROUP_PETITION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('petitionList.entities', null, 'petition list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_PETITION_LIST_MANAGE_PETITION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * petition list -> manage petition list
                     */
                    [groupName       : "GROUP_PETITION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('petitionList.entities', null, 'petition list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.disciplinary",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_DISCIPLINARY_REQUEST_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_PETITION_LIST_MANAGE_PETITION_LIST",
                             "ROLE_PERM_PETITION_LIST_EMPLOYEE_FILTER_REQUEST",
                             "ROLE_PERM_PETITION_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_PETITION_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_PETITION_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_PETITION_LIST_SEND_LIST",
                             "ROLE_PERM_PETITION_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_PETITION_LIST_RECEIVE_LIST",
                             "ROLE_PERM_PETITION_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_PETITION_LIST_CLOSE_LIST",
                             "ROLE_PERM_PETITION_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_PETITION_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_PETITION_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_PETITION_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_PETITION_LIST_NOTE_LIST",
                             "ROLE_PERM_PETITION_LIST_NOTE_CREATE",
                             "ROLE_PERM_PETITION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_PETITION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_PETITION_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * returnFromAbsenceRequest -> list
                     */
                    [groupName       : "GROUP_RETURN_FROM_ABSENCE_REQUEST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceRequest.entities', null, 'returnFromAbsenceRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_ABSENCE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ABSENCE_SHOW",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_MANAGE_RETURN_FROM_ABSENCE_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_EMPLOYEE_FILTER_REQUEST",
                     ]
                    ],

                    /**
                     * returnFromAbsenceRequest -> create
                     */
                    [groupName       : "GROUP_RETURN_FROM_ABSENCE_REQUEST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceRequest.entities', null, 'returnFromAbsenceRequest request', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_ABSENCE_AUTOCOMPLETE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_SELECT_ABSENCE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_CREATE_NEW_REQUEST",
                     ]
                    ],

                    /**
                     * returnFromAbsenceRequest -> update request
                     */
                    [groupName       : "GROUP_RETURN_FROM_ABSENCE_REQUEST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceRequest.entities', null, 'returnFromAbsenceRequest', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_ABSENCE_AUTOCOMPLETE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_SELECT_REQUEST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_SELECT_EMPLOYEE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_CREATE_NEW_REQUEST",
                     ]
                    ],

                    /**
                     * returnFromAbsenceRequest -> show
                     */
                    [groupName       : "GROUP_RETURN_FROM_ABSENCE_REQUEST_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceRequest.entities', null, 'returnFromAbsenceRequest show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_GO_TO_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_MANAGE_RETURN_FROM_ABSENCE_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_EMPLOYEE_FILTER_REQUEST",
                     ]
                    ],

                    /**
                     * returnFromAbsence list -> create
                     */
                    [groupName       : "GROUP_RETURN_FROM_ABSENCE_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceList.entities', null, 'returnFromAbsence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_MANAGE_RETURN_FROM_ABSENCE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * returnFromAbsence list -> update
                     */
                    [groupName       : "GROUP_RETURN_FROM_ABSENCE_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceList.entities', null, 'returnFromAbsence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_MANAGE_RETURN_FROM_ABSENCE_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * returnFromAbsence list -> manage returnFromAbsence list
                     */
                    [groupName       : "GROUP_RETURN_FROM_ABSENCE_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('returnFromAbsenceList.entities', null, 'returnFromAbsence list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.absence",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ABSENCE_AUTOCOMPLETE",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_MANAGE_RETURN_FROM_ABSENCE_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_EMPLOYEE_FILTER_REQUEST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_SEND_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_RECEIVE_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_CLOSE_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_NOTE_LIST",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_NOTE_CREATE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_EMPLOYEE_NOTE_SAVE"
                     ]
                    ],

                    /**
                     * applicantInspectionResultList  -> create
                     */
                    [groupName       : "GROUP_APPLICANT_INSPECTION_RESULT_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicantInspectionResultList.entities', null, 'applicantInspectionResultList create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_INSPECTION_AUTOCOMPLETE",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_MANAGE_APPLICANT_INSPECTION_RESULT_LIST"
                     ]
                    ],

                    /**
                     * applicantInspectionResultList  -> update
                     */
                    [groupName       : "GROUP_APPLICANT_INSPECTION_RESULT_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicantInspectionResultList.entities', null, 'applicantInspectionResultList update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_INSPECTION_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_INSPECTION_AUTOCOMPLETE",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_MANAGE_APPLICANT_INSPECTION_RESULT_LIST"
                     ]
                    ],

                    /**
                     * applicantInspectionResultList  -> list
                     */
                    [groupName       : "GROUP_APPLICANT_INSPECTION_RESULT_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('applicantInspectionResultList.entities', null, 'applicantInspectionResultList list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.recruitment",
                     permissionNames : [
                             "ROLE_PERM_APPLICANT_FILTER",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_MANAGE_APPLICANT_INSPECTION_RESULT_LIST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_PERSON_AUTOCOMPLETE",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_SEND_LIST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_RECEIVE_LIST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_REJECT_REQUEST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_CLOSE_LIST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_NOTE_LIST",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_NOTE_CREATE",
                             "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_EMPLOYEE_NOTE_SAVE",
                     ]
                    ],
                    /**
                     * generalList  -> create
                     */
                    [groupName       : "GROUP_GENERAL_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('generalList.entities', null, 'generalList create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.general",
                     permissionNames : [
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                             "ROLE_PERM_GENERAL_LIST_MANAGE_LIST"
                     ]
                    ],

                    /**
                     * generalList  -> update
                     */
                    [groupName       : "GROUP_GENERAL_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('generalList.entities', null, 'generalList update', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.general",
                     permissionNames : [
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                             "ROLE_PERM_GENERAL_LIST_MANAGE_LIST"
                     ]
                    ],

                    /**
                     * generalList  -> list
                     */
                    [groupName       : "GROUP_GENERAL_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('generalList.entities', null, 'generalList list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.general",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_FILTER",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",
                             "ROLE_PERM_GENERAL_LIST_MANAGE_LIST",
                             "ROLE_PERM_GENERAL_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_GENERAL_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_GENERAL_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_GENERAL_LIST_SEND_LIST",
                             "ROLE_PERM_GENERAL_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_GENERAL_LIST_RECEIVE_LIST",
                             "ROLE_PERM_GENERAL_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_GENERAL_LIST_APPROVE_REQUEST",
                             "ROLE_PERM_GENERAL_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_GENERAL_LIST_REJECT_REQUEST",
                             "ROLE_PERM_GENERAL_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_GENERAL_LIST_CLOSE_LIST",
                             "ROLE_PERM_GENERAL_LIST_NOTE_LIST",
                             "ROLE_PERM_GENERAL_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_GENERAL_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_GENERAL_LIST_NOTE_CREATE",
                             "ROLE_PERM_GENERAL_LIST_EMPLOYEE_NOTE_SAVE",
                             "ROLE_PERM_GENERAL_LIST_EMPLOYEE_FILTER",
                             "ROLE_PERM_GENERAL_LIST_EMPLOYEE_DELETE",
                             "ROLE_PERM_EMPLOYEE_SHOW"
                     ]
                    ],

                    //employee evaluation
                    [groupName       : "GROUP_EMPLOYEE_EVALUATION_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeEvaluation.entity', null, 'evaluation', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.evaluation",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_EVALUATION_SELECT_EMPLOYEE",
                             "ROLE_PERM_JOINED_EVALUATION_TEMPLATE_CATEGORY_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_EVALUATION_CREATE_NEW_EMPLOYEE_EVALUATION",
                     ]
                    ],

                    //employee evaluation
                    [groupName       : "GROUP_EMPLOYEE_EVALUATION_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeEvaluation.entity', null, 'evaluation', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.evaluation",
                     permissionNames : [
                             "ROLE_PERM_EVALUATION_CRITERIUM_AUTOCOMPLETE",
                             "ROLE_PERM_EVALUATION_TEMPLATE_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_EVALUATION_CRITERIUM_AUTOCOMPLETE",
                     ]
                    ],

                    /**
                     * evaluation list -> create
                     */
                    [groupName       : "GROUP_EVALUATION_LIST_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('evaluationList.entities', null, 'evaluation list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.evaluation",
                     permissionNames : [
                             "ROLE_PERM_EVALUATION_LIST_MANAGE_EVALUATION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * evaluation list -> update
                     */
                    [groupName       : "GROUP_EVALUATION_LIST_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('evaluationList.entities', null, 'evaluation list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.evaluation",
                     permissionNames : [
                             "ROLE_PERM_EVALUATION_LIST_MANAGE_EVALUATION_LIST",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST_MODAL",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_FILTER",
                             "ROLE_PERM_CORRESPONDENCE_TEMPLATE_GET_INSTANCE",
                     ]
                    ],

                    /**
                     * evaluation list -> manage evaluation list
                     */
                    [groupName       : "GROUP_EVALUATION_LIST_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('evaluationList.entities', null, 'evaluation list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.evaluation",
                     permissionNames : [
                             "ROLE_PERM_EVALUATION_CRITERIUM_AUTOCOMPLETE",
                             "ROLE_PERM_EVALUATION_TEMPLATE_AUTOCOMPLETE",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_EVALUATION_LIST_MANAGE_EVALUATION_LIST",
                             "ROLE_PERM_EVALUATION_LIST_EMPLOYEE_FILTER_REQUEST",
                             "ROLE_PERM_EVALUATION_LIST_ADD_REQUEST_MODAL",
                             "ROLE_PERM_EVALUATION_LIST_ADD_REQUEST_TO_LIST",
                             "ROLE_PERM_EVALUATION_LIST_SEND_LIST_MODAL",
                             "ROLE_PERM_EVALUATION_LIST_SEND_LIST",
                             "ROLE_PERM_EVALUATION_LIST_RECEIVE_LIST_MODAL",
                             "ROLE_PERM_EVALUATION_LIST_RECEIVE_LIST",
                             "ROLE_PERM_EVALUATION_LIST_CLOSE_LIST_MODAL",
                             "ROLE_PERM_EVALUATION_LIST_CLOSE_LIST",
                             "ROLE_PERM_EVALUATION_LIST_APPROVE_REQUEST_MODAL",
                             "ROLE_PERM_EVALUATION_LIST_CHANGE_REQUEST_TO_APPROVED",
                             "ROLE_PERM_EVALUATION_LIST_REJECT_REQUEST_MODAL",
                             "ROLE_PERM_EVALUATION_LIST_CHANGE_REQUEST_TO_REJECTED",
                             "ROLE_PERM_EVALUATION_LIST_NOTE_LIST",
                             "ROLE_PERM_EVALUATION_LIST_NOTE_CREATE",
                             "ROLE_PERM_EVALUATION_LIST_EMPLOYEE_NOTE_FILTER",
                             "ROLE_PERM_EVALUATION_LIST_EMPLOYEE_NOTE_DELETE",
                             "ROLE_PERM_EVALUATION_LIST_EMPLOYEE_NOTE_SAVE",
                             "ROLE_PERM_EVALUATION_LIST_EMPLOYEE_EXPORT_EXCEL",
                     ]
                    ],

                    /**
                     * manage attachment -> list
                     */
                    [groupName       : "GROUP_MANAGE_ATTACHMENT_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('manageAttachment.entities', null, 'manageAttachment list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.common",
                     permissionNames : [
                             "ROLE_PERM_ATTACHMENT_FILTER_ATTACHMENT",

                     ]
                    ],

                    //joinedFirmOperationDocument
                    [groupName       : "GROUP_JOINED_FIRM_OPERATION_DOCUMENT_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('joinedFirmOperationDocument.entity', null, 'joinedFirmOperationDocument', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.settings",
                     permissionNames : [
                             "ROLE_PERM_JOINED_FIRM_OPERATION_DOCUMENT_GET_OPERATION_SELECT_ELEMENT",
                             "ROLE_PERM_FIRM_DOCUMENT_AUTOCOMPLETE",
                             "ROLE_PERM_JOINED_FIRM_OPERATION_DOCUMENT_SAVE",
                             "ROLE_PERM_JOINED_FIRM_OPERATION_DOCUMENT_CREATE"
                     ]
                    ],

                    //Department -> create
                    [groupName       : "GROUP_DEPARTMENT_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('Department.entity', null, 'Department', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm",
                     permissionNames : [
                             "ROLE_PERM_JOINED_FIRM_OPERATION_DOCUMENT_GET_OPERATION_SELECT_ELEMENT",
                     ]
                    ],

                    //Department -> edit
                    [groupName       : "GROUP_DEPARTMENT_EDIT",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('Department.entity', null, 'Department', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm",
                     permissionNames : [
                             "ROLE_PERM_DEPARTMENT_TYPE_AUTOCOMPLETE",
                     ]
                    ],

                    //PersonArrestHistory -> list
                    [groupName       : "GROUP_PERSON_ARREST_HISTORY_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('PersonArrestHistory.entity', null, 'Department', arabicLocal),
                     groupCagtegory  : "ps.police.pcore.v2.entity.person",
                     permissionNames : [
                             "ROLE_PERM_PERSON_ARREST_HISTORY_PRE_CREATE",
                     ]
                    ],

                    //profileNotice
                    [groupName       : "GROUP_PROFILE_NOTICE_SHOW",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('profileNotice.entities', null, 'profileNotice show', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.profileNotice",
                     permissionNames : [
                             "ROLE_PERM_PROFILE_NOTICE_SHOW",
                             "ROLE_PERM_EMPLOYEE_SAVE_EMPLOYEE_PROFILE_STATUS",
                             "ROLE_PERM_PROFILE_NOTICE_SAVE_CHANGE_STATUS"
                     ]
                    ],
                    /**
                     * province  -> create
                     */
                    [groupName       : "GROUP_PROVINCE_CREATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('province.entities', null, 'province create', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.province",
                     permissionNames : [
                             "ROLE_PERM_PROVINCE_LOCATION_MODAL",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                     ]
                    ],

                    /**
                     * province  -> update
                     */
                    [groupName       : "GROUP_PROVINCE_UPDATE",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('province.entities', null, 'province edit', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.hr.firm.province",
                     permissionNames : [
                             "ROLE_PERM_PROVINCE_LOCATION_MODAL",
                             "ROLE_PERM_PCORE_COUNTRY_AUTO_COMPLETE",
                             "ROLE_PERM_PCORE_GOVERNORATE_AUTO_COMPLETE",
                     ]
                    ],

                    /**
                     * province  -> update
                     */
                    [groupName       : "GROUP_EMPLOYEE_SALARY_INFO_LIST",
                     groupDescription: securityPluginBootStrapService?.messageSource?.getMessage('employeeSalaryInfo.entities', null, 'employeeSalaryInfo list', arabicLocal),
                     groupCagtegory  : "ps.gov.epsilon.aoc.firm.employee",
                     permissionNames : [
                             "ROLE_PERM_EMPLOYEE_SALARY_INFO_IMPORT_FINANCIAL_DATA",
                             "ROLE_PERM_EMPLOYEE_SALARY_INFO_SHOW",
                             "ROLE_PERM_EMPLOYEE_AUTOCOMPLETE",
                             "ROLE_PERM_MILITARY_RANK_AUTOCOMPLETE",
                             "ROLE_PERM_FIRM_AUTOCOMPLETE",
                             "ROLE_PERM_PCORE_ORGANIZATION_AUTO_COMPLETE",
                             "ROLE_PERM_CURRENCY_AUTOCOMPLETE",
                     ]
                    ],

            ]

            GroupBean groupBean
            List<Permission> permissionList
            permissionGroups.each { Map entry ->
                groupBean = new GroupBean()
                groupBean.groupName = entry.groupName
                groupBean.groupDescription = entry.groupDescription
                groupBean.groupCategory = entry.groupCagtegory
                groupBean.groupCategory = entry.groupCagtegory
                groupBean.permissionBeanList = []
                permissionList = Permission.findAllByPermissionNameInList(entry.permissionNames)
                permissionList.each { Permission permissionInstance ->
                    groupBean.permissionBeanList << new PermissionBean(
                            permissionName: permissionInstance?.permissionName,
                            actionName: permissionInstance?.actionName,
                            controllerName: permissionInstance?.controllerName,
                            actionUrl: permissionInstance?.actionUrl,
                            permissionInstance: permissionInstance
                    )
                    securityPluginBootStrapService.saveGroupAndPermissions(groupBean)
                }
            }
            println "... End Saving Custom Groups and Permissions"
        }
    }

    void initMenu() {
        Map menuList = [

                //home
                "home"                               : [message: "الرئيسيه", controller: "home", action: "index", icon: "icon-home", authorities: "isFullyAuthenticated", menuLevel: MenuLevel.PARENT],

                /*//firm
                "firm"                                     : [message: "المؤسسة", authorities: "", menuLevel: MenuLevel.PARENT],*/

                //organizationalStructure
                "organizationalStructure"            : [message: "الهيكل التنظيمي", controller: "structure", action: "index", icon: "icon-flow-tree", authorities: "ROLE_PERM_STRUCTURE_INDEX", menuLevel: MenuLevel.PARENT],
                /*//jobsDescriptions
                "jobsDescriptions"                         : [message: "التوصيف الوظيفي", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "firm"],
                "jobsDescriptionsList"                     : [message: "قائمة", icon: "icon-list", controller: "", action: "list", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "jobsDescriptions"],
                "jobsDescriptionsCreate"                   : [message: "إنشاء", icon: "icon-plus-circled", controller: "", action: "create", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "jobsDescriptions"],
                //reports
                "reports"                                  : [message: "التقارير", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "firm"],
                "reportsList"                              : [message: "قائمة", icon: "icon-list", controller: "", action: "list", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "reports"],
                "reportsCreate"                            : [message: "إنشاء", icon: "icon-plus-circled", controller: "", action: "create", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "reports"],*/

                //recruitment
                "recruitment"                        : [message: "التجنيد", icon: "icon-users-2", authorities: "", menuLevel: MenuLevel.PARENT],
                //RecruitmentCycle
                "recruitmentCycle"                   : [message: "الدورات التجنيدية", controller: "recruitmentCycle", action: "list", authorities: "ROLE_PERM_RECRUITMENT_CYCLE_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "recruitment"],
                //jobRequisition
                "jobRequisition"                     : [message: "طلبات الإحتياج", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "recruitment"],
                "jobRequisitionList"                 : [message: "قائمة", icon: "icon-list", controller: "jobRequisition", action: "list", authorities: "ROLE_PERM_JOB_REQUISITION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "jobRequisition"],
                "jobRequisitionCreate"               : [message: "إنشاء", icon: "icon-plus-circled", controller: "jobRequisition", action: "create", authorities: "ROLE_PERM_JOB_REQUISITION_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "jobRequisition"],
                "approvejobRequisition"              : [message: "معالجة طلبات الإحتياج", icon: "icon-list", controller: "jobRequisition", action: "listManager", authorities: "ROLE_PERM_JOB_REQUISITION_LIST_MANAGER", menuLevel: MenuLevel.SUB_PARENT_1, parent: "jobRequisition"],
                //vacancy
                "vacancy"                            : [message: "الشواغر", controller: "vacancy", action: "list", authorities: "ROLE_PERM_VACANCY_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "recruitment"],
                //vacancyAdvertisements
                "vacancyAdvertisements"              : [message: "إعلانات", controller: "vacancyAdvertisements", action: "list", authorities: "ROLE_PERM_VACANCY_ADVERTISEMENTS_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "recruitment"],
                //applicant
                "applicant"                          : [message: "المتنافسون", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "recruitment"],
                "applicantList"                      : [message: "قائمة", icon: "icon-list", controller: "applicant", action: "list", authorities: "ROLE_PERM_APPLICANT_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "applicant"],
                "applicantCreate"                    : [message: "إنشاء", icon: "icon-plus-circled", controller: "applicant", action: "create", authorities: "ROLE_PERM_APPLICANT_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "applicant"],
                //interviews
                "interviews"                         : [message: "المقابلات", controller: "interview", action: "list", authorities: "ROLE_PERM_INTERVIEW_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "recruitment"],

                //employees
                "employees"                          : [message: "الموظفون", icon: "icon-user", authorities: "", menuLevel: MenuLevel.PARENT],
                //employee
                "employee"                           : [message: "الموظف", authorities: "", icon: "icon-user", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                "employeeList"                       : [message: "قائمة", icon: "icon-list", controller: "employee", action: "list", authorities: "ROLE_PERM_EMPLOYEE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employee"],
                "employeeCreate"                     : [message: "إنشاء", icon: "icon-plus-circled", controller: "employee", action: "create", authorities: "ROLE_PERM_EMPLOYEE_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employee"],

                "profileNotice"                      : [message: "قائمة البلاغات", icon: "icon-list", controller: "profileNotice", action: "list", authorities: "ROLE_PERM_PROFILE_NOTICE_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                //promotion
//                "promotion"                        : [message: "الترقيات العسكرية", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
//                "promotionList"                    : [message: "قائمة", icon: "icon-list", controller: "promotion", action: "list", authorities: "ROLE_PERM_PROMOTION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "promotion"],
//                "promotionCreate"                  : [message: "إنشاء", icon: "icon-plus-circled", controller: "promotion", action: "create", authorities: "ROLE_PERM_PROMOTION_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "promotion"],

                //transfer
                "transfer"                           : [message: "النقل", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                //internalTransfer
                "internalTransfer"                   : [message: "نقل داخلي", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "transfer"],
                "internalTransferList"               : [message: "قائمة", icon: "icon-list", controller: "internalTransferRequest", action: "list", authorities: "ROLE_PERM_INTERNAL_TRANSFER_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "internalTransfer"],
                "internalTransferCreate"             : [message: "إنشاء", icon: "icon-plus-circled", controller: "internalTransferRequest", action: "create", authorities: "ROLE_PERM_INTERNAL_TRANSFER_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "internalTransfer"],
                //externalTransfer
                "externalTransfer"                   : [message: "نقل خارجي", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "transfer"],
                "externalTransferList"               : [message: "قائمة", icon: "icon-list", controller: "externalTransferRequest", action: "list", authorities: "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "externalTransfer"],
                "externalTransferCreate"             : [message: "إنشاء", icon: "icon-plus-circled", controller: "externalTransferRequest", action: "create", authorities: "ROLE_PERM_EXTERNAL_TRANSFER_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "externalTransfer"],
                //receiveTransferPerson
                "receiveTransferPerson"              : [message: "توثيق الشخص المنقول", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "transfer"],
                "receiveTransferPersonList"          : [message: "قائمة", icon: "icon-list", controller: "externalReceivedTransferredPerson", action: "list", authorities: "ROLE_PERM_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "receiveTransferPerson"],
                "receiveTransferPersonCreate"        : [message: "إنشاء", icon: "icon-plus-circled", controller: "externalReceivedTransferredPerson", action: "create", authorities: "ROLE_PERM_EXTERNAL_RECEIVED_TRANSFERRED_PERSON_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "receiveTransferPerson"],

                //loan
                "loan"                               : [message: "الندب", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],

                "loanRequest"                        : [message: "طلب ندب الى الجهاز", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "loan"],
                "loanRequestList"                    : [message: "قائمة", icon: "icon-list", controller: "loanRequest", action: "list", authorities: "ROLE_PERM_LOAN_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanRequest"],
                "loanRequestCreate"                  : [message: "إنشاء", icon: "icon-plus-circled", controller: "loanRequest", action: "create", authorities: "ROLE_PERM_LOAN_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanRequest"],

                "loanRequestRelatedPerson"           : [message: "توثيق الاشخاص المنتدبين", icon: "icon-list", controller: "loanRequestRelatedPerson", action: "list", authorities: "ROLE_PERM_LOAN_REQUEST_RELATED_PERSON_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "loan"],

                "loanNotice"                         : [message: "طلب ندب من الجهاز", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "loan"],
                "loanNoticeList"                     : [message: "قائمة", icon: "icon-list", controller: "loanNotice", action: "list", authorities: "ROLE_PERM_LOAN_NOTICE_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanNotice"],
                "loanNoticeCreate"                   : [message: "إنشاء", icon: "icon-plus-circled", controller: "loanNotice", action: "create", authorities: "ROLE_PERM_LOAN_NOTICE_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanNotice"],

                "endorseOrder"                       : [message: "توثيق اوامر التسيير", icon: "icon-list", controller: "loanNominatedEmployee", action: "list", authorities: "ROLE_PERM_LOAN_NOMINATED_EMPLOYEE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "loan"],



                "employeeSalaryInfo"                 : [message: "المعلومات المالية", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                "employeeSalaryInfoList"             : [message: "قائمة", icon: "icon-list", controller: "employeeSalaryInfo", action: "list", authorities: "ROLE_PERM_EMPLOYEE_SALARY_INFO_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeSalaryInfo"],

                //dispatchRequest
                "dispatchRequest"                    : [message: "الايفاد", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                "dispatchRequestList"                : [message: "قائمة", icon: "icon-list", controller: "dispatchRequest", action: "list", authorities: "ROLE_PERM_DISPATCH_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "dispatchRequest"],
                "dispatchRequestCreate"              : [message: "إنشاء", icon: "icon-plus-circled", controller: "dispatchRequest", action: "create", authorities: "ROLE_PERM_DISPATCH_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "dispatchRequest"],

                //employeeEvaluation
                "employeeEvaluation"                 : [message: "التقييمات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                "employeeEvaluationList"             : [message: "قائمة", icon: "icon-list", controller: "employeeEvaluation", action: "list", authorities: "ROLE_PERM_EMPLOYEE_EVALUATION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeEvaluation"],
                "employeeEvaluationCreate"           : [message: "إنشاء", icon: "icon-plus-circled", controller: "employeeEvaluation", action: "create", authorities: "ROLE_PERM_EMPLOYEE_EVALUATION_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeEvaluation"],





                "suspensionRequest"                  : [message: "الإستيداع", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                "suspensionRequestList"              : [message: "قائمة", icon: "icon-list", controller: "suspensionRequest", action: "list", authorities: "ROLE_PERM_SUSPENSION_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "suspensionRequest"],
                "suspensionRequestCreate"            : [message: "إنشاء", icon: "icon-plus-circled", controller: "suspensionRequest", action: "create", authorities: "ROLE_PERM_SUSPENSION_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "suspensionRequest"],


                "personArrestHistory"                : [message: "سجل الاعتقال", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "employees"],
                "personArrestHistoryList"            : [message: "قائمة", icon: "icon-list", controller: "personArrestHistory", action: "list", authorities: "ROLE_PERM_PERSON_ARREST_HISTORY_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "personArrestHistory"],
                "personArrestHistoryCreate"          : [message: "إنشاء", icon: "icon-plus-circled", controller: "personArrestHistory", action: "preCreate", authorities: "ROLE_PERM_PERSON_ARREST_HISTORY_PRE_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "personArrestHistory"],

                //vacations
                "vacations"                          : [message: "الإجازات", icon: "icon-calendar-inv", authorities: "", menuLevel: MenuLevel.PARENT],
                //vacation request
                "vacationRequest"                    : [message: "طلب إجازة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "vacations"],
                "vacationsList"                      : [message: "قائمة", icon: "icon-list", controller: "vacationRequest", action: "list", authorities: "ROLE_PERM_VACATION_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacationRequest"],
                "vacationsCreate"                    : [message: "إنشاء", icon: "icon-plus-circled", controller: "vacationRequest", action: "create", authorities: "ROLE_PERM_VACATION_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacationRequest"],
                //vacations
                "vacationRequestList"                : [message: "طلب إجازة جماعية", authorities: "ROLE_PERM_VACATION_REQUEST_CREATE", controller: "vacationRequest", action: "createNewListVacationRequest", menuLevel: MenuLevel.SUB_PARENT, parent: "vacations"],
                "employeeVacationBalance"            : [message: "ترصيد  الاجازات", controller: "employeeVacationBalance", action: "list", authorities: "ROLE_PERM_EMPLOYEE_VACATION_BALANCE_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "vacations"],
                "showEmployeeBalance"                : [message: "عرض رصيد الموظف", controller: "employeeVacationBalance", action: "showEmployeeBalance", authorities: "ROLE_PERM_EMPLOYEE_VACATION_BALANCE_SHOW_EMPLOYEE_BALANCE", menuLevel: MenuLevel.SUB_PARENT, parent: "vacations"],

                //bordersSecurityCoordination
                "bordersSecurityCoordination"        : [message: "تنسيق السفر", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "vacations"],
                "bordersSecurityCoordinationList"    : [message: "قائمة", icon: "icon-list", controller: "bordersSecurityCoordination", action: "list", authorities: "ROLE_PERM_BORDERS_SECURITY_COORDINATION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "bordersSecurityCoordination"],
                "bordersSecurityCoordinationCreate"  : [message: "إنشاء", icon: "icon-plus-circled", controller: "bordersSecurityCoordination", action: "create", authorities: "ROLE_PERM_BORDERS_SECURITY_COORDINATION_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "bordersSecurityCoordination"],

                //promotionRequest
                "promotionRequests"                  : [message: "الترقيات", icon: "icon-award-1", authorities: "", menuLevel: MenuLevel.PARENT],
                //updateMilitaryRankRequest
                "updateMilitaryRankRequest"          : [message: "طلبات تعديل نوع/صفة الرتبة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "promotionRequests"],
                "updateMilitaryRankRequestList"      : [message: "قائمة", icon: "icon-list", controller: "updateMilitaryRankRequest", action: "list", authorities: "ROLE_PERM_UPDATE_MILITARY_RANK_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "updateMilitaryRankRequest"],
                "updateMilitaryRankRequestCreate"    : [message: "إنشاء", icon: "icon-plus-circled", controller: "updateMilitaryRankRequest", action: "create", authorities: "ROLE_PERM_UPDATE_MILITARY_RANK_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "updateMilitaryRankRequest"],

                //promotionRequest
                "promotionRequest"                   : [message: "طلبات الترقية", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "promotionRequests"],
                "promotionRequestList"               : [message: "قائمة", icon: "icon-list", controller: "promotionRequest", action: "list", authorities: "ROLE_PERM_PROMOTION_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "promotionRequest"],
                "promotionRequestCreate"             : [message: "إنشاء", icon: "icon-plus-circled", controller: "promotionRequest", action: "create", authorities: "ROLE_PERM_PROMOTION_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "promotionRequest"],

                "allowances"                         : [message: "العلاوات", icon: "icon-money", authorities: "", menuLevel: MenuLevel.PARENT],

                "allowanceRequest"                   : [message: " طلب علاوة", authorities: "ROLE_PERM_ALLOWANCE_REQUEST_LIST", controller: "allowanceRequest", action: "list", menuLevel: MenuLevel.SUB_PARENT, parent: "allowances"],
                "allowanceRequestList"               : [message: "قائمة", icon: "icon-list", controller: "allowanceRequest", action: "list", authorities: "ROLE_PERM_ALLOWANCE_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "allowanceRequest"],
                "allowanceRequestCreate"             : [message: "إنشاء", icon: "icon-plus-circled", controller: "allowanceRequest", action: "create", authorities: "ROLE_PERM_ALLOWANCE_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "allowanceRequest"],




                "maritalStatus"                      : [message: "الحالة الاجتماعية", icon: "icon-user-pair", authorities: "", menuLevel: MenuLevel.PARENT],

                //maritalStatusRequest
                "maritalStatusRequest"               : [message: "تعديل على الحالة الاجتماعية", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "maritalStatus"],
                "maritalStatusRequestList"           : [message: "قائمة", icon: "icon-list", controller: "maritalStatusRequest", action: "list", authorities: "ROLE_PERM_MARITAL_STATUS_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "maritalStatusRequest"],
                "maritalStatusRequestCreate"         : [message: "إنشاء", icon: "icon-plus-circled", controller: "maritalStatusRequest", action: "create", authorities: "ROLE_PERM_MARITAL_STATUS_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "maritalStatusRequest"],

                //childRequest
                "childRequest"                       : [message: "إضافة مواليد", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "maritalStatus"],
                "childRequestList"                   : [message: "قائمة", icon: "icon-list", controller: "childRequest", action: "list", authorities: "ROLE_PERM_CHILD_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "childRequest"],
                "childRequestCreate"                 : [message: "إنشاء", icon: "icon-plus-circled", controller: "childRequest", action: "create", authorities: "ROLE_PERM_CHILD_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "childRequest"],

                //disciplinaryAndViolation
                "disciplinaryAndViolation"           : [message: "المخالفات والعقوبات", icon: "icon-roadblock", authorities: "", menuLevel: MenuLevel.PARENT],

                //employeeViolation
                "employeeViolation"                  : [message: "المخالفات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "disciplinaryAndViolation"],
                "employeeViolationList"              : [message: "قائمة", icon: "icon-list", controller: "employeeViolation", action: "list", authorities: "ROLE_PERM_EMPLOYEE_VIOLATION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeViolation"],
                "employeeViolationCreate"            : [message: "إنشاء", icon: "icon-plus-circled", controller: "employeeViolation", action: "create", authorities: "ROLE_PERM_EMPLOYEE_VIOLATION_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeViolation"],

                //disciplinaryRequest
                "disciplinaryRequest"                : [message: "العقوبات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "disciplinaryAndViolation"],
                "disciplinaryRequestList"            : [message: "قائمة", icon: "icon-list", controller: "disciplinaryRequest", action: "list", authorities: "ROLE_PERM_DISCIPLINARY_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "disciplinaryRequest"],
                "disciplinaryRequestCreate"          : [message: "إنشاء", icon: "icon-plus-circled", controller: "disciplinaryRequest", action: "create", authorities: "ROLE_PERM_DISCIPLINARY_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "disciplinaryRequest"],

                //petitionRequest
                "petitionRequest"                    : [message: "طلبات الاسترحام", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "disciplinaryAndViolation"],
                "petitionRequestList"                : [message: "قائمة", icon: "icon-list", controller: "petitionRequest", action: "list", authorities: "ROLE_PERM_PETITION_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "petitionRequest"],
                "petitionRequestCreate"              : [message: "إنشاء", icon: "icon-plus-circled", controller: "petitionRequest", action: "create", authorities: "ROLE_PERM_PETITION_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "petitionRequest"],

                //absence
                "absence"                            : [message: "الغياب", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "disciplinaryAndViolation"],
                "absenceList"                        : [message: "قائمة", icon: "icon-list", controller: "absence", action: "list", authorities: "ROLE_PERM_ABSENCE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "absence"],
                "absenceCreate"                      : [message: "إنشاء", icon: "icon-plus-circled", controller: "absence", action: "create", authorities: "ROLE_PERM_ABSENCE_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "absence"],

                //ReturnFromAbsenceRequest
                "returnFromAbsenceRequest"           : [message: "إشعار عودة من غياب", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "disciplinaryAndViolation"],
                "returnFromAbsenceRequestList"       : [message: "قائمة", icon: "icon-list", controller: "returnFromAbsenceRequest", action: "list", authorities: "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "returnFromAbsenceRequest"],
                "returnFromAbsenceRequestCreate"     : [message: "إنشاء", icon: "icon-plus-circled", controller: "returnFromAbsenceRequest", action: "create", authorities: "ROLE_PERM_RETURN_FROM_ABSENCE_REQUEST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "returnFromAbsenceRequest"],

                //ServiceProcedures
                "ServiceProcedures"                  : [message: "اجراءات الخدمة", icon: "icon-archive", authorities: "", menuLevel: MenuLevel.PARENT],
                //endOfServiceRequest
                "endOfServiceRequest"                : [message: "انهاء الخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "ServiceProcedures"],
                "endOfServiceRequestList"            : [message: "قائمة", icon: "icon-list", controller: "employmentServiceRequest", action: "listEndOfService", authorities: "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_LIST_END_OF_SERVICE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "endOfServiceRequest"],
                "endOfServiceRequestCreate"          : [message: "إنشاء", icon: "icon-plus-circled", controller: "employmentServiceRequest", action: "redirectEndOfService", authorities: "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_REDIRECT_END_OF_SERVICE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "endOfServiceRequest"],
//                //returnToServiceRequest
                "returnToServiceRequest"             : [message: "الاستدعاء للخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "ServiceProcedures"],
                "returnToServiceRequestList"         : [message: "قائمة", icon: "icon-list", controller: "employmentServiceRequest", action: "listReturnToService", authorities: "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_LIST_RETURN_TO_SERVICE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "returnToServiceRequest"],
                "returnToServiceRequestCreate"       : [message: "إنشاء", icon: "icon-plus-circled", controller: "employmentServiceRequest", action: "redirectReturnToService", authorities: "ROLE_PERM_EMPLOYMENT_SERVICE_REQUEST_REDIRECT_RETURN_TO_SERVICE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "returnToServiceRequest"],

                /*//systemControl
                "systemControl"                            : [message: "إدارة النظام", authorities: "", menuLevel: MenuLevel.PARENT],*/
                //menuControl
                "menuControl"                        : [message: "إدارة المراسلات", icon: "icon-mail-alt", authorities: "", menuLevel: MenuLevel.PARENT],

                //recruitmentLists
                "recruitmentLists"                   : [message: "التجنيد", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "applicantInspectionResultList"      : [message: "مراسلات الفحوصات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "recruitmentLists"],
                "applicantInspectionResultListList"  : [message: "قائمة", icon: "icon-list", controller: "applicantInspectionResultList", action: "list", authorities: "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "applicantInspectionResultList"],
                "applicantInspectionResultListCreate": [message: "إنشاء", icon: "icon-plus-circled", controller: "applicantInspectionResultList", action: "create", authorities: "ROLE_PERM_APPLICANT_INSPECTION_RESULT_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "applicantInspectionResultList"],

                "traineeList"                        : [message: "مراسلات تدريب المستجدين", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "recruitmentLists"],
                "traineeListList"                    : [message: "قائمة", icon: "icon-list", controller: "traineeList", action: "list", authorities: "ROLE_PERM_TRAINEE_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "traineeList"],
                "traineeListCreate"                  : [message: "إنشاء", icon: "icon-plus-circled", controller: "traineeList", action: "create", authorities: "ROLE_PERM_TRAINEE_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "traineeList"],

                "recruitmentList"                    : [message: "مراسلات التجنيد", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "recruitmentLists"],
                "recruitmentListList"                : [message: "قائمة", icon: "icon-list", controller: "recruitmentList", action: "list", authorities: "ROLE_PERM_RECRUITMENT_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "recruitmentList"],
                "recruitmentListCreate"              : [message: "إنشاء", icon: "icon-plus-circled", controller: "recruitmentList", action: "create", authorities: "ROLE_PERM_RECRUITMENT_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "recruitmentList"],

                //employeeLists
                "employeeLists"                      : [message: "الموظف", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "transferList"                       : [message: "مراسلات النقل الخارجية", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLists"],
                "externalTransferListList"           : [message: "قائمة", icon: "icon-list", controller: "externalTransferList", action: "list", authorities: "ROLE_PERM_EXTERNAL_TRANSFER_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "transferList"],
                "externalTransferListCreate"         : [message: "إنشاء", icon: "icon-plus-circled", controller: "externalTransferList", action: "create", authorities: "ROLE_PERM_EXTERNAL_TRANSFER_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "transferList"],

                "loanList"                           : [message: "مراسلات ندب إلى الجهاز", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLists"],
                "loanListList"                       : [message: "قائمة", icon: "icon-list", controller: "loanList", action: "list", authorities: "ROLE_PERM_LOAN_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanList"],
                "loanListCreate"                     : [message: "إنشاء", icon: "icon-plus-circled", controller: "loanList", action: "create", authorities: "ROLE_PERM_LOAN_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanList"],

                "loanNoticeReplayList"               : [message: "مراسلات ندب من الجهاز", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLists"],
                "loanNoticeReplayListList"           : [message: "قائمة", icon: "icon-list", controller: "loanNoticeReplayList", action: "list", authorities: "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanNoticeReplayList"],
                "loanNoticeReplayListCreate"         : [message: "إنشاء", icon: "icon-plus-circled", controller: "loanNoticeReplayList", action: "create", authorities: "ROLE_PERM_LOAN_NOTICE_REPLAY_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "loanNoticeReplayList"],

                "dispatchList"                       : [message: "مراسلات الايفاد", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLists"],
                "dispatchListList"                   : [message: "قائمة", icon: "icon-list", controller: "dispatchList", action: "list", authorities: "ROLE_PERM_DISPATCH_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "dispatchList"],
                "dispatchListCreate"                 : [message: "إنشاء", icon: "icon-plus-circled", controller: "dispatchList", action: "create", authorities: "ROLE_PERM_DISPATCH_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "dispatchList"],

                "suspensionList"                     : [message: "مراسلات الإستيداع", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLists"],
                "suspensionListList"                 : [message: "قائمة", icon: "icon-list", controller: "suspensionList", action: "list", authorities: "ROLE_PERM_SUSPENSION_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "suspensionList"],
                "suspensionListCreate"               : [message: "إنشاء", icon: "icon-plus-circled", controller: "suspensionList", action: "create", authorities: "ROLE_PERM_SUSPENSION_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "suspensionList"],

                "suspensionExtensionList"            : [message: "مراسلات تمديد الاستيداع", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLists"],
                "suspensionExtensionListList"        : [message: "قائمة", icon: "icon-list", controller: "suspensionExtensionList", action: "list", authorities: "ROLE_PERM_SUSPENSION_EXTENSION_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "suspensionExtensionList"],
                "suspensionExtensionListCreate"      : [message: "إنشاء", icon: "icon-plus-circled", controller: "suspensionExtensionList", action: "create", authorities: "ROLE_PERM_SUSPENSION_EXTENSION_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "suspensionExtensionList"],

                //vacationLists
                "vacationLists"                      : [message: "الاجازات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "vacationList"                       : [message: "مراسلات الإجازات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacationLists"],
                "vacationListList"                   : [message: "قائمة", icon: "icon-list", controller: "vacationList", action: "list", authorities: "ROLE_PERM_VACATION_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "vacationList"],
                "vacationListCreate"                 : [message: "إنشاء", icon: "icon-plus-circled", controller: "vacationList", action: "create", authorities: "ROLE_PERM_VACATION_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "vacationList"],

                //promotionLists
                "promotionLists"                     : [message: "الترقيات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "promotionList"                      : [message: "مراسلات الترقيات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "promotionLists"],
                "promotionListList"                  : [message: "قائمة", icon: "icon-list", controller: "promotionList", action: "list", authorities: "ROLE_PERM_PROMOTION_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "promotionList"],
                "promotionListCreate"                : [message: "إنشاء", icon: "icon-plus-circled", controller: "promotionList", action: "create", authorities: "ROLE_PERM_PROMOTION_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "promotionList"],

                "evaluationList"                     : [message: "مراسلات التقييمات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "promotionLists"],
                "evaluationListList"                 : [message: "قائمة", icon: "icon-list", controller: "evaluationList", action: "list", authorities: "ROLE_PERM_EVALUATION_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "evaluationList"],
                "evaluationListCreate"               : [message: "إنشاء", icon: "icon-plus-circled", controller: "evaluationList", action: "create", authorities: "ROLE_PERM_EVALUATION_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "evaluationList"],

                //allowanceLists
                "allowanceLists"                     : [message: "العلاوات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "allowanceList"                      : [message: "مراسلات العلاوات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "allowanceLists"],
                "allowanceListList"                  : [message: "قائمة", icon: "icon-list", controller: "allowanceList", action: "list", authorities: "ROLE_PERM_ALLOWANCE_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "allowanceList"],
                "allowanceListCreate"                : [message: "إنشاء", icon: "icon-plus-circled", controller: "allowanceList", action: "create", authorities: "ROLE_PERM_ALLOWANCE_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "allowanceList"],

                //maritalStatusLists
                "maritalStatusLists"                 : [message: "الحالة الاجتماعية", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "maritalStatusList"                  : [message: "مراسلات التعديل على الحالة الاجتماعية", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "maritalStatusLists"],
                "maritalStatusListList"              : [message: "قائمة", icon: "icon-list", controller: "maritalStatusList", action: "list", authorities: "ROLE_PERM_MARITAL_STATUS_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "maritalStatusList"],
                "maritalStatusListCreate"            : [message: "إنشاء", icon: "icon-plus-circled", controller: "maritalStatusList", action: "create", authorities: "ROLE_PERM_MARITAL_STATUS_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "maritalStatusList"],

                "childList"                          : [message: "مراسلات المواليد", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "maritalStatusLists"],
                "childListList"                      : [message: "قائمة", icon: "icon-list", controller: "childList", action: "list", authorities: "ROLE_PERM_CHILD_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "childList"],
                "childListCreate"                    : [message: "إنشاء", icon: "icon-plus-circled", controller: "childList", action: "create", authorities: "ROLE_PERM_CHILD_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "childList"],

                //violationAndDisciplinaryLists
                "violationAndDisciplinaryLists"      : [message: "المخالفات والعقوبات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "violationList"                      : [message: "مراسلات المخالفات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "violationAndDisciplinaryLists"],
                "violationListList"                  : [message: "قائمة", controller: "violationList", action: "list", authorities: "ROLE_PERM_VIOLATION_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "violationList"],
                "violationListCreate"                : [message: "إنشاء", controller: "violationList", action: "create", authorities: "ROLE_PERM_VIOLATION_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "violationList"],

                "disciplinaryList"                   : [message: "مراسلات العقوبات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "violationAndDisciplinaryLists"],
                "disciplinaryListList"               : [message: "قائمة", icon: "icon-list", controller: "disciplinaryList", action: "list", authorities: "ROLE_PERM_DISCIPLINARY_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "disciplinaryList"],
                "disciplinaryListCreate"             : [message: "إنشاء", icon: "icon-plus-circled", controller: "disciplinaryList", action: "create", authorities: "ROLE_PERM_DISCIPLINARY_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "disciplinaryList"],


                "returnFromAbsenceList"              : [message: "مراسلات اشعارات العودة", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "violationAndDisciplinaryLists"],
                "returnFromAbsenceListList"          : [message: "قائمة", controller: "returnFromAbsenceList", action: "list", authorities: "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "returnFromAbsenceList"],
                "returnFromAbsenceListCreate"        : [message: "إنشاء", controller: "returnFromAbsenceList", action: "create", authorities: "ROLE_PERM_RETURN_FROM_ABSENCE_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "returnFromAbsenceList"],

                "petitionList"                       : [message: "مراسلات طلبات الاسترحام", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "violationAndDisciplinaryLists"],
                "petitionListList"                   : [message: "قائمة", icon: "icon-list", controller: "petitionList", action: "list", authorities: "ROLE_PERM_PETITION_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "petitionList"],
                "petitionListCreate"                 : [message: "إنشاء", icon: "icon-plus-circled", controller: "petitionList", action: "create", authorities: "ROLE_PERM_PETITION_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_2, parent: "petitionList"],

                //ServiceLists
                "ServiceLists"                       : [message: "اجراءات الخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],

                "endOfServiceList"                   : [message: "مراسلات انهاء الخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "ServiceLists"],
                "endOfServiceListList"               : [message: "قائمة", icon: "icon-list", controller: "serviceList", action: "listEndOfServiceList", authorities: "ROLE_PERM_SERVICE_LIST_LIST_END_OF_SERVICE_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "endOfServiceList"],
                "endOfServiceListCreate"             : [message: "إنشاء", icon: "icon-plus-circled", controller: "serviceList", action: "createEndOfServiceList", authorities: "ROLE_PERM_SERVICE_LIST_CREATE_END_OF_SERVICE_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "endOfServiceList"],

                "returnToServiceList"                : [message: "مراسلات الاستدعاء للخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "ServiceLists"],
                "returnToServiceListList"            : [message: "قائمة", icon: "icon-list", controller: "serviceList", action: "listReturnToServiceList", authorities: "ROLE_PERM_SERVICE_LIST_LIST_RETURN_TO_SERVICE_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "returnToServiceList"],
                "returnToServiceListCreate"          : [message: "إنشاء", icon: "icon-plus-circled", controller: "serviceList", action: "createReturnToServiceList", authorities: "ROLE_PERM_SERVICE_LIST_CREATE_RETURN_TO_SERVICE_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "returnToServiceList"],



                "generalList"                        : [message: "مراسلات عامة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "menuControl"],
                "generalListList"                    : [message: "قائمة", icon: "icon-list", controller: "generalList", action: "list", authorities: "ROLE_PERM_GENERAL_LIST_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "generalList"],
                "generalListCreate"                  : [message: "إنشاء", icon: "icon-plus-circled", controller: "generalList", action: "create", authorities: "ROLE_PERM_GENERAL_LIST_CREATE", menuLevel: MenuLevel.SUB_PARENT_1, parent: "generalList"],

                /*//controlPanel
                "controlPanel"                             : [message: "لوحة التحكم", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "systemControl"],
                //settings
                "settings"                                 : [message: "إعدادات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "systemControl"],
                //systemAttachments
                "systemAttachments"                        : [message: "مرفقات النظام", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "systemControl"],
*/
                // AOC menu
                "aocList"                            : [message: "هيئة التنظيم والإدارة", icon: "icon-commerical-building", authorities: "", menuLevel: MenuLevel.PARENT],

                //Incoming and Outgoing
                "AocInOut"                           : [message: "الصادر والوارد", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "createOutgoing"                     : [message: "إنشاء صادر", controller: "aocCorrespondenceList", action: "createOutgoing", authorities: "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "AocInOut"],
                // Aoc Create incoming
                "createIncoming"                     : [message: "إنشاء وارد", controller: "aocCorrespondenceList", action: "createIncoming", authorities: "ROLE_PERM_AOC_CORRESPONDENCE_LIST_CREATE_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "AocInOut"],

                //Incoming and Outgoing
                "aocAllowanceList"                   : [message: "مراسلات العلاوات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocAllowanceListOutgoing"           : [message: "قائمة الصادر", controller: "aocAllowanceList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_ALLOWANCE_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocAllowanceList"],
                // Aoc Create incoming
                "aocAllowanceListIncoming"           : [message: "قائمة الوارد", controller: "aocAllowanceList", action: "listIncoming", authorities: "ROLE_PERM_AOC_ALLOWANCE_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocAllowanceList"],

                //Incoming and Outgoing
                "aocPromotionList"                   : [message: "مراسلات الترقيات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocPromotionListOutgoing"           : [message: "قائمة الصادر", controller: "aocPromotionList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_PROMOTION_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocPromotionList"],
                // Aoc Create incoming
                "aocPromotionListIncoming"           : [message: "قائمة الوارد", controller: "aocPromotionList", action: "listIncoming", authorities: "ROLE_PERM_AOC_PROMOTION_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocPromotionList"],

                //Incoming and Outgoing
                "aocChildList"                       : [message: "مراسلات المواليد", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocChildListOutgoing"               : [message: "قائمة الصادر", controller: "AocChildList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_CHILD_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocChildList"],
                // Aoc Create incoming
                "aocChildListIncoming"               : [message: "قائمة الوارد", controller: "AocChildList", action: "listIncoming", authorities: "ROLE_PERM_AOC_CHILD_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocChildList"],

                //Incoming and Outgoing
                "aocViolationList"                   : [message: "مراسلات المخالفات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocViolationListOutgoing"           : [message: "قائمة الصادر", controller: "AocViolationList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_VIOLATION_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocViolationList"],
                // Aoc Create incoming
                "aocViolationListIncoming"           : [message: "قائمة الوارد", controller: "AocViolationList", action: "listIncoming", authorities: "ROLE_PERM_AOC_VIOLATION_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocViolationList"],

                //Incoming and Outgoing
                "aocMaritalStatusList"               : [message: "مراسلات التعديل على الحالة الاجتماعية", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocMaritalStatusListOutgoing"       : [message: "قائمة الصادر", controller: "AocMaritalStatusList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_MARITAL_STATUS_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocMaritalStatusList"],
                // Aoc Create incoming
                "aocMaritalStatusListIncoming"       : [message: "قائمة الوارد", controller: "AocMaritalStatusList", action: "listIncoming", authorities: "ROLE_PERM_AOC_MARITAL_STATUS_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocMaritalStatusList"],

                //Incoming and Outgoing
                "aocExternalTransferList"            : [message: "مراسلات النقل الخارجي", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],
                // Aoc Create outgoing
                "aocExternalTransferListOutgoing"    : [message: "قائمة الصادر", controller: "AocExternalTransferList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_EXTERNAL_TRANSFER_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocExternalTransferList"],
                // Aoc Create incoming
                "aocExternalTransferListIncoming"    : [message: "قائمة الوارد", controller: "AocExternalTransferList", action: "listIncoming", authorities: "ROLE_PERM_AOC_EXTERNAL_TRANSFER_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocExternalTransferList"],

                //Incoming and Outgoing
                "aocDisciplinaryList"                : [message: "مراسلات العقوبات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocDisciplinaryListOutgoing"        : [message: "قائمة الصادر", controller: "AocDisciplinaryList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_DISCIPLINARY_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocDisciplinaryList"],
                // Aoc Create incoming
                "aocDisciplinaryListIncoming"        : [message: "قائمة الوارد", controller: "AocDisciplinaryList", action: "listIncoming", authorities: "ROLE_PERM_AOC_DISCIPLINARY_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocDisciplinaryList"],

                //Incoming and Outgoing
                "aocEvaluationList"                  : [message: "مراسلات التقييمات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocEvaluationListOutgoing"          : [message: "قائمة الصادر", controller: "AocEvaluationList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_EVALUATION_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocEvaluationList"],
                // Aoc Create incoming
                "aocEvaluationListIncoming"          : [message: "قائمة الوارد", controller: "AocEvaluationList", action: "listIncoming", authorities: "ROLE_PERM_AOC_EVALUATION_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocEvaluationList"],

                //Incoming and Outgoing
                "aocVacationList"                    : [message: "مراسلات الاجازات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],
                // Aoc Create outgoing
                "aocVacationListOutgoing"            : [message: "قائمة الصادر", controller: "aocVacationList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_VACATION_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocVacationList"],
                // Aoc Create incoming
                "aocVacationListIncoming"            : [message: "قائمة الوارد", controller: "aocVacationList", action: "listIncoming", authorities: "ROLE_PERM_AOC_VACATION_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocVacationList"],

                //Incoming and Outgoing
                "aocReturnFromAbsenceList"           : [message: "مراسلات اشعارات العودة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],
                // Aoc Create outgoing
                "aocReturnFromAbsenceListOutgoing"   : [message: "قائمة الصادر", controller: "aocReturnFromAbsenceList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_RETURN_FROM_ABSENCE_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocReturnFromAbsenceList"],
                // Aoc Create incoming
                "aocReturnFromAbsenceListIncoming"   : [message: "قائمة الوارد", controller: "aocReturnFromAbsenceList", action: "listIncoming", authorities: "ROLE_PERM_AOC_RETURN_FROM_ABSENCE_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocReturnFromAbsenceList"],

                //Incoming and Outgoing
                "aocEndOfServiceList"                : [message: "مراسلات انهاء الخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocEndOfServiceListOutgoing"        : [message: "قائمة الصادر", controller: "AocEndOfServiceList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_END_OF_SERVICE_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocEndOfServiceList"],
                // Aoc Create incoming
                "aocEndOfServiceListIncoming"        : [message: "قائمة الوارد", controller: "AocEndOfServiceList", action: "listIncoming", authorities: "ROLE_PERM_AOC_END_OF_SERVICE_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocEndOfServiceList"],

                //Incoming and Outgoing
                "aocReturnToServiceList"             : [message: "مراسلات استدعاء للخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],

                // Aoc Create outgoing
                "aocReturnToServiceListOutgoing"     : [message: "قائمة الصادر", controller: "AocReturnToServiceList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_RETURN_TO_SERVICE_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocReturnToServiceList"],
                // Aoc Create incoming
                "aocReturnToServiceListIncoming"     : [message: "قائمة الوارد", controller: "AocReturnToServiceList", action: "listIncoming", authorities: "ROLE_PERM_AOC_RETURN_TO_SERVICE_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocReturnToServiceList"],

                //Incoming and Outgoing
                "aocSuspensionList"                  : [message: "مراسلات الاستيداع", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],
                // Aoc Create outgoing
                "aocSuspensionListOutgoing"          : [message: "قائمة الصادر", controller: "aocSuspensionList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_SUSPENSION_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocSuspensionList"],
                // Aoc Create incoming
                "aocSuspensionListIncoming"          : [message: "قائمة الوارد", controller: "aocSuspensionList", action: "listIncoming", authorities: "ROLE_PERM_AOC_SUSPENSION_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocSuspensionList"],

                //Incoming and Outgoing
                "aocLoanList"                        : [message: "مراسلات الندب الى الجهاز", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],
                // Aoc Create outgoing
                "aocLoanListOutgoing"                : [message: "قائمة الصادر", controller: "aocLoanList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_LOAN_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocLoanList"],
                // Aoc Create incoming
                "aocLoanListIncoming"                : [message: "قائمة الوارد", controller: "aocLoanList", action: "listIncoming", authorities: "ROLE_PERM_AOC_LOAN_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocLoanList"],

                //Incoming and Outgoing
                "aocLoanNoticeReplayList"            : [message: "مراسلات الندب من الجهاز", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],
                // Aoc Create outgoing
                "aocLoanNoticeReplayListOutgoing"    : [message: "قائمة الصادر", controller: "aocLoanNoticeReplayList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocLoanNoticeReplayList"],
                // Aoc Create incoming
                "aocLoanNoticeReplayListIncoming"    : [message: "قائمة الوارد", controller: "aocLoanNoticeReplayList", action: "listIncoming", authorities: "ROLE_PERM_AOC_LOAN_NOTICE_REPLAY_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocLoanNoticeReplayList"],

                //Incoming and Outgoing
                "aocDispatchList"                    : [message: "مراسلات الايفاد", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "aocList"],
                // Aoc Create outgoing
                "aocDispatchListOutgoing"            : [message: "قائمة الصادر", controller: "aocDispatchList", action: "listOutgoing", authorities: "ROLE_PERM_AOC_DISPATCH_LIST_LIST_OUTGOING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocDispatchList"],
                // Aoc Create incoming
                "aocDispatchListIncoming"            : [message: "قائمة الوارد", controller: "aocDispatchList", action: "listIncoming", authorities: "ROLE_PERM_AOC_DISPATCH_LIST_LIST_INCOMING", menuLevel: MenuLevel.SUB_PARENT_1, parent: "aocDispatchList"],

                //systemReport
                "systemReport"                       : [message: "التقارير", controller: "systemReport", action: "list", authorities: "ROLE_PERM_SYSTEM_REPORT_LIST", icon: "icon-docs", menuLevel: MenuLevel.PARENT],

                //systemAttachment
                "systemAttachment"                   : [message: "المرفقات", controller: "manageAttachment", action: "list", authorities: "ROLE_PERM_MANAGE_ATTACHMENT_LIST", icon: "icon-docs", menuLevel: MenuLevel.PARENT],

                /**
                 * request
                 */

                "request"                            : [message: "معالجة مسار العمل", icon: " icon-doc-text-inv", authorities: "", menuLevel: MenuLevel.PARENT],
                "requestWaitingApproval"             : [message: "الطلبات بإنتظار الموافقة", icon: "icon-list", authorities: "ROLE_PERM_REQUEST_LIST", controller: 'request', action: 'list', menuLevel: MenuLevel.SUB_PARENT, parent: "request"],
                "aocCorrespondenceListWorkflow"      : [message: "المراسلات بإنتظار الموافقة", icon: "icon-list", authorities: "ROLE_PERM_AOC_CORRESPONDENCE_LIST_LIST_WORKFLOW", controller: 'aocCorrespondenceList', action: 'listWorkflow', menuLevel: MenuLevel.SUB_PARENT, parent: "request"],

                //lookups
                "lookups"                            : [message: "الجداول الثابتة", icon: "icon-list", authorities: "", menuLevel: MenuLevel.PARENT],

                //firm
                "firmLookups"                        : [message: "المؤسسة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                //firm
                "firms"                              : [message: "المؤسسة", controller: "firm", action: "list", authorities: "ROLE_PERM_FIRM_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],
                //operationalTask
                "operationalTask"                    : [message: "المهمات الوظيفية في المؤسسة", controller: "operationalTask", action: "list", authorities: "ROLE_PERM_OPERATIONAL_TASK_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],
                //departmentTypes
                "departmentTypes"                    : [message: "أنواع الوحدة الادارية", controller: "departmentType", action: "list", authorities: "ROLE_PERM_DEPARTMENT_TYPE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],
                //departments
                "departments"                        : [message: "الوحدة الادارية", controller: "department", action: "list", authorities: "ROLE_PERM_DEPARTMENT_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],
                //firmSetting
                "firmSetting"                        : [message: "اعدادت النظام", controller: "firmSetting", action: "list", authorities: "ROLE_PERM_FIRM_SETTING_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],
                //firmActiveModule
/*
                "firmActiveModule"                         : [message: "اعدادات وحدات النظام", controller: "firmActiveModule", action: "list", authorities: "ROLE_PERM_FIRM_ACTIVE_MODULE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],
*/
                //firmDocument
                "firmDocument"                       : [message: "وثائق النظام", controller: "firmDocument", action: "list", authorities: "ROLE_PERM_FIRM_DOCUMENT_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],
                //joinedFirmOperationDocument
                "joinedFirmOperationDocument"        : [message: "ربط الوثائق بالإجراءات", controller: "joinedFirmOperationDocument", action: "list", authorities: "ROLE_PERM_JOINED_FIRM_OPERATION_DOCUMENT_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "firmLookups"],

                //employeeLookups
                "employeeLookups"                    : [message: "الموظفون", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                //employeeStatusCategory
                "employeeStatusCategory"             : [message: "حالة الموظف", controller: "employeeStatusCategory", action: "list", authorities: "ROLE_PERM_EMPLOYEE_STATUS_CATEGORY_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLookups"],
                //employeeStatus
                "employeeStatus"                     : [message: "حالة الدوام", controller: "employeeStatus", action: "list", authorities: "ROLE_PERM_EMPLOYEE_STATUS_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLookups"],
                //militaryRank
                "militaryRank"                       : [message: "رتبة عسكرية", controller: "militaryRank", action: "list", authorities: "ROLE_PERM_MILITARY_RANK_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLookups"],
                // militaryRanksTypes
                "militaryRanksTypes"                 : [message: "نوع الرتبة العسكرية", controller: "militaryRankType", action: "list", authorities: "ROLE_PERM_MILITARY_RANK_TYPE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLookups"],
                // militaryRanksTypes
                "militaryRankClassifications"        : [message: "صفة الرتبة العسكرية", controller: "militaryRankClassification", action: "list", authorities: "ROLE_PERM_MILITARY_RANK_CLASSIFICATION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLookups"],
                //employmentCategory
                "employmentCategory"                 : [message: "تصنيف الموظف", controller: "employmentCategory", action: "list", authorities: "ROLE_PERM_EMPLOYMENT_CATEGORY_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "employeeLookups"],

                //evaluationLookups
                "evaluationLookups"                  : [message: "التقييمات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                "evaluationTemplate"                 : [message: "نماذج التقييم", controller: "evaluationTemplate", action: "list", authorities: "ROLE_PERM_EVALUATION_TEMPLATE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "evaluationLookups"],
                "evaluationSection"                  : [message: "أقسام نموذج التقييم", controller: "evaluationSection", action: "list", authorities: "ROLE_PERM_EVALUATION_SECTION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "evaluationLookups"],
                "evaluationItem"                     : [message: "أسئلة نموذج التقييم", controller: "evaluationItem", action: "list", authorities: "ROLE_PERM_EVALUATION_ITEM_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "evaluationLookups"],
                "evaluationCriterium"                : [message: "معايير التقييم", controller: "evaluationCriterium", action: "list", authorities: "ROLE_PERM_EVALUATION_CRITERIUM_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "evaluationLookups"],

                //vacationLookups
                "vacationLookups"                    : [message: "الاجازات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                "vacationType"                       : [message: "انواع الاجازات", controller: "vacationType", action: "list", authorities: "ROLE_PERM_VACATION_TYPE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacationLookups"],
                "vacationConfiguration"              : [message: "اعدادات  الاجازات", controller: "vacationConfiguration", action: "list", authorities: "ROLE_PERM_VACATION_CONFIGURATION_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacationLookups"],

                //allowanceLookups
                "allowanceLookups"                   : [message: "العلاوات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                "allowanceType"                      : [message: "انواع العلاوات", controller: "allowanceType", action: "list", authorities: "ROLE_PERM_ALLOWANCE_TYPE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "allowanceLookups"],
                "allowanceStopReason"                : [message: "أسباب وقف العلاوة", controller: "allowanceStopReason", action: "list", authorities: "ROLE_PERM_ALLOWANCE_STOP_REASON_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "allowanceLookups"],

                //disciplinaryLookups
                "disciplinaryLookups"                : [message: "العقوبات", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                //disciplinaryCategories
                "disciplinaryCategories"             : [message: "تصنيف المخالفة", controller: "disciplinaryCategory", action: "list", authorities: "ROLE_PERM_DISCIPLINARY_CATEGORY_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "disciplinaryLookups"],
                //disciplinaryReason
                "disciplinaryReason"                 : [message: "المخالفة", controller: "disciplinaryReason", action: "list", authorities: "ROLE_PERM_DISCIPLINARY_REASON_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "disciplinaryLookups"],
                //disciplinaryJudgment
                "disciplinaryJudgment"               : [message: "العقوبة", controller: "disciplinaryJudgment", action: "list", authorities: "ROLE_PERM_DISCIPLINARY_JUDGMENT_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "disciplinaryLookups"],
                //disciplinaryJudgment
/*
                "disciplinaryListJudgmentSetup"            : [message: "اعدادات قائمة حكم العقوبات", controller: "disciplinaryListJudgmentSetup", action: "list", authorities: "ROLE_PERM_DISCIPLINARY_LIST_JUDGMENT_SETUP_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "disciplinaryLookups"],
*/

                //vacancies
                "vacancies"                          : [message: "الوظائف", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                //jobCategory
                "jobCategory"                        : [message: "التصنيف الوظيفي", controller: "jobCategory", action: "list", authorities: "ROLE_PERM_JOB_CATEGORY_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacancies"],
                //jobTitle
                "jobTitle"                           : [message: "المسمى الوظيفي", controller: "jobTitle", action: "list", authorities: "ROLE_PERM_JOB_TITLE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacancies"],
                //jobRequirements
                "jobRequirement"                     : [message: " متطلبات الوظيفة", controller: "JobRequirement", action: "list", authorities: "ROLE_PERM_JOB_REQUIREMENT_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacancies"],
                //job
                "job"                                : [message: "وظيفة", controller: "job", action: "list", authorities: "ROLE_PERM_JOB_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "vacancies"],

                /*             //attendanceType
                             "attendanceType"                           : [message: "المناوبات", controller: "attendanceType", action: "list", authorities: "ROLE_PERM_ATTENDANCE_TYPE_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
             */
                //Recruitment
                "RecruitmentLookups"                 : [message: "التجنيد", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                //committeeRole
                "committeeRole"                      : [message: "تصنيف العضوية في اللجان", controller: "committeeRole", action: "list", authorities: "ROLE_PERM_COMMITTEE_ROLE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "RecruitmentLookups"],
                //inspections
                "inspections"                        : [message: "الفحوصات", authorities: "", menuLevel: MenuLevel.SUB_PARENT_1, parent: "RecruitmentLookups"],
                //inspectionsCategories
                "inspectionsCategories"              : [message: "تصنيف الفحوصات", controller: "inspectionCategory", action: "list", authorities: "ROLE_PERM_INSPECTION_CATEGORY_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "inspections"],
                //inspection
                "inspection"                         : [message: "الفحوصات", controller: "inspection", action: "list", authorities: "ROLE_PERM_INSPECTION_LIST", menuLevel: MenuLevel.SUB_PARENT_2, parent: "inspections"],
                //trainingRejectionReason
                "trainingRejectionReason"            : [message: "أسباب رفض المتدرب", controller: "trainingRejectionReason", action: "list", authorities: "ROLE_PERM_TRAINING_REJECTION_REASON_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "RecruitmentLookups"],
                //jobType
                "jobType"                            : [message: "نوع العقد", controller: "jobType", action: "list", authorities: "ROLE_PERM_JOB_TYPE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "RecruitmentLookups"],

                //trainingLookups
                "training"                           : [message: "تصنيف الدورات", controller: "trainingClassification", action: "list", authorities: "ROLE_PERM_TRAINING_CLASSIFICATION_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],


                "serviceActionReasonLookups"         : [message: "اجراءات الخدمة", authorities: "", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                "serviceActionReasonType"            : [message: "أنواع اجراءات الخدمة", controller: "serviceActionReasonType", action: "list", authorities: "ROLE_PERM_SERVICE_ACTION_REASON_TYPE_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "serviceActionReasonLookups"],
                "serviceActionReason"                : [message: "أسباب اجراءات الخدمة", controller: "serviceActionReason", action: "list", authorities: "ROLE_PERM_SERVICE_ACTION_REASON_LIST", menuLevel: MenuLevel.SUB_PARENT_1, parent: "serviceActionReasonLookups"],

                //correspondenceTemplate
                "CorrespondenceTemplate"             : [message: "نماذج المراسلة", controller: "CorrespondenceTemplate", action: "list", authorities: "ROLE_PERM_CORRESPONDENCE_TEMPLATE_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],

                //workflow
                "workflow"                           : [message: "المسار", controller: "workflow", action: "list", authorities: "ROLE_PERM_WORKFLOW_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],
                "province"                           : [message: "الاقاليم", controller: "province", action: "list", authorities: "ROLE_PERM_PROVINCE_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "lookups"],

                //audit
                "audit"                              : [message: "سجلات التدقيق", controller: "auditLogEvent", action: "list", authorities: "ROLE_PERM_AUDIT_LOG_EVENT_LIST", menuLevel: MenuLevel.PARENT],


                "aocLookups"                         : [message: "الجداول الثابتة للهيئة", icon: "icon-list", authorities: "", menuLevel: MenuLevel.PARENT],
                "committee"                          : [message: "اللجان", controller: "committee", action: "list", authorities: "ROLE_PERM_COMMITTEE_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "aocLookups"],
                "profileNoticeCategory"              : [message: "تصنيفات البلاغ", controller: "profileNoticeCategory", action: "list", authorities: "ROLE_PERM_PROFILE_NOTICE_CATEGORY_LIST", menuLevel: MenuLevel.SUB_PARENT, parent: "aocLookups"],

        ]

        if (!NavigationItem.findByControllerAndAction("home", "index")) {
            Map<String, NavigationItem> items = [:]
            NavigationItem parent
            String sortOrder = ""
            Map counters = [:]
            menuList.each { Map.Entry<String, LinkedHashMap<String, Comparable>> entry ->
                if (!counters.get(entry.value.menuLevel)) {
                    counters[entry.value.menuLevel] = 0
                }
                if (entry.value.parent) {
                    parent = items["${entry.value.parent}"]
                    resetBelowCounter(counters, entry.value.menuLevel)
                    sortOrder = parent?.sortOrder + "_" + generateNumber(counters[entry.value.menuLevel], 2)
                    counters[entry.value.menuLevel]++
                } else {
                    sortOrder = "00_" + generateNumber(counters[entry.value.menuLevel], 2)
                    counters[entry.value.menuLevel]++
                    resetBelowCounter(counters, entry.value.menuLevel);
                }
                NavigationItem item = NavigationItem.findBySortOrder(sortOrder)
                if (!item) {
                    entry?.value?.remove("parent")
                    entry?.value?.remove("menuLevel")
                    item = new NavigationItem(entry?.value)
                    item.sortOrder = sortOrder
                    if (parent) {
                        item?.parent = NavigationItem.findBySortOrder(parent?.sortOrder)
                    }
                    println("... Saving NavigationItem ${item?.message}")
                    item.save(flush: true, failOnError: true)
                }
                items[entry?.key] = item
                parent = null
                sortOrder = null
            }
        }

        //assign authorities to navigation item
        List<NavigationItem> navigationItemList = NavigationItem.findAllByAuthoritiesIsNull()
        String authorities = ""
        navigationItemList.each { NavigationItem navigationItem ->
            List itemList = NavigationItem.findAllByAuthoritiesIsNotNullAndSortOrderLike("${navigationItem?.sortOrder}%")
            authorities = itemList?.authorities?.join(",")
            navigationItem.authorities = authorities
            navigationItem.save(flush: true, failOnError: true)
        }
    }

    void createNotificationType() {
        def notificationTypes = [
                [localName: "إشعاراتي", latinName: 'my notification', topic: "myNotification", icon: 'icon-bell-alt'],
                [localName: "موافقاتي", latinName: 'workflow', topic: "workflowMessages", icon: 'icon-bell-alt'],
                [localName: "مراسلات", latinName: 'lists', topic: "listMessages", icon: 'icon-mail']
        ]
        DescriptionInfo descriptionInfo
        NotificationType notificationType

        notificationTypes?.each {
            descriptionInfo = new DescriptionInfo(localName: it.localName, latinName: it.latinName)
            notificationType = NotificationType.createCriteria().get {
                eq('latinName', descriptionInfo?.latinName)
            }
            if (!notificationType) {
                notificationType = new NotificationType(descriptionInfo: descriptionInfo, topic: it?.topic).save(failOnError: true, flush: true)
                println "Fresh Database. Creating ${it?.localName} notification type."
            }
        }
    }

    void createFirmDocument() {
        List<Firm> firmList = Firm.list()
        def firmDocuments = [
                [id: 1, localName: "صورة شخصية", latinName: 'Personal Picture'],
        ]
        DescriptionInfo descriptionInfo
        FirmDocument firmDocument

        firmList.each { Firm firm ->

            firmDocuments?.each { document ->
                descriptionInfo = new DescriptionInfo(localName: document.localName, latinName: document?.latinName)
                firmDocument = FirmDocument.createCriteria().get {
                    eq('latinName', descriptionInfo?.latinName)
                    eq('firm.id', firm?.id)
                }
                if (!firmDocument) {
                    firmDocument = new FirmDocument(id: document?.id, descriptionInfo: descriptionInfo, firm: firm).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${document?.localName} firm document for firm ${firm?.name}."
                }
            }
        }
    }

    void createStatusCategories() {
        List<Firm> firmList = Firm.list()
        def employeeStatusCategories = [
                [id: 1, localName: "ملتزم", latinName: 'Committed'],
                [id: 2, localName: "غير ملتزم", latinName: 'Uncommitted'],
        ]
        def employeeCategories = [

                //PLEASE DO NOT CHANGE THE SORTING AND IF YOU WANT ADD NEW ADD AT THE END OF EACH ONE

                //committed status
                [id: 1, localName: "على رأس عمله", latinName: 'Working', employeeStatusCategoryLatinName: 'Committed'],
                [id: 2, localName: "غائب", latinName: 'Absence', employeeStatusCategoryLatinName: 'Committed'],
                [id: 3, localName: "موفد", latinName: 'Dispatched', employeeStatusCategoryLatinName: 'Committed'],
                [id: 4, localName: "منتدب لجهاز آخر", latinName: 'Loan Out', employeeStatusCategoryLatinName: 'Committed'],
                [id: 12, localName: "مجاز", latinName: 'In Vacation', employeeStatusCategoryLatinName: 'Committed'],

                //un committed status
                [id: 5, localName: "منقول", latinName: 'Transferred', employeeStatusCategoryLatinName: 'Uncommitted'],
                [id: 6, localName: "منتدب لدي", latinName: 'Loan In', employeeStatusCategoryLatinName: 'Uncommitted'],
                [id: 7, localName: "متقاعد", latinName: 'Retirement', employeeStatusCategoryLatinName: 'Uncommitted'],
                [id: 8, localName: "مفصول", latinName: 'Firing', employeeStatusCategoryLatinName: 'Uncommitted'],
                [id: 9, localName: "مستقيل", latinName: 'Resignation', employeeStatusCategoryLatinName: 'Uncommitted'],
                [id: 10, localName: "متوفى", latinName: 'Death', employeeStatusCategoryLatinName: 'Uncommitted'],
                [id: 11, localName: "يدرس", latinName: 'Studying', employeeStatusCategoryLatinName: 'Uncommitted'],
                [id: 13, localName: "مستودع", latinName: 'Suspended', employeeStatusCategoryLatinName: 'Uncommitted'],
        ]

        DescriptionInfo descriptionInfo
        DescriptionInfo employeeDescriptionInfo
        EmployeeStatusCategory employeeStatusCategory
        EmployeeStatus employeeStatus

        firmList.each { Firm firm ->

            employeeStatusCategories?.each { category ->
                descriptionInfo = new DescriptionInfo(localName: category.localName, latinName: category?.latinName)
                employeeStatusCategory = EmployeeStatusCategory.createCriteria().get {
                    eq('latinName', descriptionInfo?.latinName)
                    eq('firm.id', firm?.id)
                }
                if (!employeeStatusCategory) {
                    employeeStatusCategory = new EmployeeStatusCategory(id: category?.id, descriptionInfo: descriptionInfo, firm: firm).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${category?.localName} employee status category for firm ${firm?.name}."
                }

                employeeCategories.each { status ->
                    if (category.latinName == status.employeeStatusCategoryLatinName) {
                        employeeDescriptionInfo = new DescriptionInfo(localName: status.localName, latinName: status.latinName)
                        employeeStatus = EmployeeStatus.createCriteria().get {
                            eq('latinName', employeeDescriptionInfo?.latinName)
                            eq('firm.id', firm?.id)
                            eq('employeeStatusCategory', employeeStatusCategory)
                        }

                        if (!employeeStatus) {
                            employeeStatus = new EmployeeStatus(id: status?.id, descriptionInfo: employeeDescriptionInfo, firm: firm, employeeStatusCategory: employeeStatusCategory).save(failOnError: true, flush: true)
                            println "Fresh Database. Creating ${status?.localName} employee status for firm ${firm?.name}."
                        }
                    }
                }
            }
        }
    }

    void createServiceActionReason() {
        List<Firm> firmList = Firm.list()
        def serviceActionReasonTypes = [
                [id: 1, localName: "استدعاء للعمل", latinName: 'ReturnToService']
        ]
        def serviceActionReasons = [
                [id: 1, localName: "انهاء تقاعد", latinName: 'Retirement', serviceActionReasonTypeLatinName: 'ReturnToService'],
                [id: 2, localName: "انهاء استيداع", latinName: 'Suspension', serviceActionReasonTypeLatinName: 'ReturnToService']
        ]

        DescriptionInfo descriptionInfo
        DescriptionInfo reasonDescriptionInfo
        ServiceActionReasonType serviceActionReasonType
        ServiceActionReason serviceActionReason
        EmployeeStatus employeeStatusResult
        EmployeeStatusCategory employeeStatusCategory

        firmList.each { Firm firm ->
            employeeStatusCategory = EmployeeStatusCategory.createCriteria().get {
                eq('latinName', "Committed")
                eq('firm.id', firm?.id)
            }
            employeeStatusResult = EmployeeStatus.createCriteria().get {
                eq('latinName', "Working")
                eq('firm.id', firm?.id)
                eq('employeeStatusCategory', employeeStatusCategory)
            }
            serviceActionReasonTypes?.each { type ->
                descriptionInfo = new DescriptionInfo(localName: type.localName, latinName: type?.latinName)
                serviceActionReasonType = ServiceActionReasonType.createCriteria().get {
                    eq('latinName', descriptionInfo?.latinName)
                    eq('firm.id', firm?.id)
                }
                if (!serviceActionReasonType) {
                    serviceActionReasonType = new ServiceActionReasonType(id: type?.id, descriptionInfo: descriptionInfo, firm: firm).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${type?.localName} serviceActionReasonType for firm ${firm?.name}."
                }
                serviceActionReasons.each { reason ->
                    if (type.latinName == reason.serviceActionReasonTypeLatinName) {
                        reasonDescriptionInfo = new DescriptionInfo(localName: reason.localName, latinName: reason.latinName)
                        serviceActionReason = ServiceActionReason.createCriteria().get {
                            eq('latinName', reasonDescriptionInfo?.latinName)
                            eq('firm.id', firm?.id)
                            eq('serviceActionReasonType', serviceActionReasonType)
                        }
                        if (!serviceActionReason) {
                            serviceActionReason = new ServiceActionReason(id: reason?.id, descriptionInfo: reasonDescriptionInfo, employeeStatusResult: employeeStatusResult, firm: firm, serviceActionReasonType: serviceActionReasonType).save(failOnError: true, flush: true)
                            println "Fresh Database. Creating ${reason?.localName} serviceActionReason for firm ${firm?.name}."
                        }
                    }
                }
            }
        }
    }

    void createMilitaryRanks() {

        def dataList = [
                [localName: "جندي", latinName: 'soldier', orderNo: new Short("1"), numberOfYearToPromote: new Short("3")],
                [localName: "عريف", latinName: 'Corporal', orderNo: new Short("2"), numberOfYearToPromote: new Short("3")],
                [localName: "رقيب", latinName: 'Sergeant', orderNo: new Short("3"), numberOfYearToPromote: new Short("4")],
                [localName: "رقيب أول", latinName: 'staff Sergeant', orderNo: new Short("4"), numberOfYearToPromote: new Short("4")],
                [localName: "مساعد", latinName: 'Assistant', orderNo: new Short("5"), numberOfYearToPromote: new Short("4")],
                [localName: "مساعد أول", latinName: 'First Assistant', orderNo: new Short("6"), numberOfYearToPromote: new Short("4")],
                [localName: "ملازم", latinName: 'lieutenant', orderNo: new Short("7"), numberOfYearToPromote: new Short("3")],
                [localName: "ملازم أول", latinName: 'first lieutenant', orderNo: new Short("8"), numberOfYearToPromote: new Short("4")],
                [localName: "نقيب", latinName: 'captain', orderNo: new Short("9"), numberOfYearToPromote: new Short("4")],
                [localName: "رائد", latinName: 'Pioneer', orderNo: new Short("10"), numberOfYearToPromote: new Short("5")],
                [localName: "مقدم", latinName: 'Lieutenant Colonel', orderNo: new Short("11"), numberOfYearToPromote: new Short("5")],
                [localName: "عقيد", latinName: 'Colonel', orderNo: new Short("12"), numberOfYearToPromote: new Short("5")],
                [localName: "عميد", latinName: 'dean', orderNo: new Short("13"), numberOfYearToPromote: new Short("4")],
                [localName: "لواء", latinName: 'major General', orderNo: new Short("14"), numberOfYearToPromote: new Short("3")],
                [localName: "فريق", latinName: 'Team', orderNo: new Short("15"), numberOfYearToPromote: null],
        ]

        DescriptionInfo descriptionInfo
        MilitaryRank dataInstance

        dataList?.each { record ->
            descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record?.latinName)
            Firm.list()?.each { firmRecord ->
                dataInstance = MilitaryRank.createCriteria().get {
                    eq('localName', descriptionInfo?.localName)
                    eq('firm.id', firmRecord?.id)
                }
                if (!dataInstance) {
                    dataInstance = new MilitaryRank(descriptionInfo: descriptionInfo, firm: firmRecord, orderNo: record?.orderNo, numberOfYearToPromote: record?.numberOfYearToPromote).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${dataInstance?.descriptionInfo} Military Rank for firm ${firmRecord?.name}."
                }
            }
        }

    }

    void createJobCategories() {

        try {
            List<Firm> firmList = Firm.list()
            def dataList = [
                    [id: 1, localName: "مدني", latinName: 'Civil Employee'],
                    [id: 2, localName: "عسكري", latinName: 'Soldier'],
                    [id: 3, localName: "نائب رئيس وحدة", latinName: 'Deputy Head Of Unit'],
                    [id: 4, localName: "رئيس وحدة", latinName: 'Head Of Unit'],
                    [id: 5, localName: "نائب رئيس قسم", latinName: 'Deputy Head Of Section'],
                    [id: 6, localName: "رئيس قسم", latinName: 'Head Of Section'],
                    [id: 7, localName: "نائب مدير دائرة", latinName: 'Deputy Head Of Department'],
                    [id: 8, localName: "مدير دائرة", latinName: 'Head Of Department'],
                    [id: 9, localName: "نائب مدير محافظة", latinName: 'Deputy Head Of Governorate'],
                    [id: 10, localName: "مدير محافظة", latinName: 'Head Of Governorate'],
                    [id: 11, localName: "نائب مدير ادارة عامة", latinName: 'Deputy Head Of Firm'],
                    [id: 12, localName: "مدير ادارة عامة", latinName: 'Deputy Head Of Firm'],
                    [id: 13, localName: "نائب مدير جهاز", latinName: 'Deputy Head Of Firm'],
                    [id: 14, localName: "مدير جهاز", latinName: 'Head Of Firm']
            ]
            DescriptionInfo descriptionInfo
            JobCategory dataInstance

            firmList.each { Firm firm ->

                dataList?.each { record ->
                    descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record?.latinName)
                    dataInstance = JobCategory.createCriteria().get {
                        or {
                            eq('localName', descriptionInfo?.localName)
                            eq("id", firm?.code + "-" + record.id)
                        }
                        eq('firm.id', firm?.id)
                    }
                    if (!dataInstance) {
                        dataInstance = new JobCategory(id: record?.id, descriptionInfo: descriptionInfo, firm: firm).save(failOnError: true, flush: true)
                        println "Fresh Database. Creating ${dataInstance?.descriptionInfo} job category for firm ${firm?.name}."
                    }
                }
            }
        } catch (Exception e) {
            println "Fail to save job category."
        }

    }

    void createEmploymentCategories() {
        List<Firm> firmList = Firm.list()
        def dataList = [
                [id: 1, localName: "طالب", latinName: 'Student'],
                [id: 2, localName: "عسكري", latinName: 'Soldier'],
        ]
        DescriptionInfo descriptionInfo
        EmploymentCategory dataInstance

        firmList.each { Firm firm ->

            dataList?.each { record ->
                descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record?.latinName)
                dataInstance = EmploymentCategory.createCriteria().get {
                    eq('localName', descriptionInfo?.localName)
                    eq('firm.id', firm?.id)
                }
                if (!dataInstance) {
                    dataInstance = new EmploymentCategory(id: record?.id, descriptionInfo: descriptionInfo, firm: firm).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${dataInstance?.descriptionInfo} employment category for firm ${firm?.name}."
                }
            }
        }
    }

    /**
     * create default vacation type
     */
    void createVacationType() {
        List<Firm> firmList = Firm.list()
        def dataList = [
                [id: 1, localName: "إجازة سنوية", latinName: 'annual vacation'],
        ]
        DescriptionInfo descriptionInfo
        VacationType dataInstance

        firmList.each { Firm firm ->

            dataList?.each { record ->
                descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record.latinName)
                dataInstance = VacationType.createCriteria().get {
                    eq('latinName', descriptionInfo?.latinName)
                    eq('firm.id', firm?.id)
                }
                if (!dataInstance) {
                    dataInstance = new VacationType(id: record?.id, descriptionInfo: descriptionInfo, firm: firm, colorId: 1L, excludedFromServicePeriod: false).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${dataInstance?.descriptionInfo} vacation type for firm ${firm?.name}."
                }
            }
        }
    }

    /**
     * create default vacation type
     */
    void createAllowanceType() {
        List<Firm> firmList = Firm.list()
        def dataList = [
                [id: 1, localName: "علاوة ابن", latinName: 'SON', relationshipTypeId: RelationshipTypeEnum.SON.value()],
                [id: 2, localName: "علاوة ابنة", latinName: 'DAUGHTER', relationshipTypeId: RelationshipTypeEnum.DAUGHTER.value()],
                [id: 3, localName: "علاوة زوجة", latinName: 'WIFE', relationshipTypeId: RelationshipTypeEnum.WIFE.value()],
                [id: 4, localName: "علاوة زوج", latinName: 'HUSBAND', relationshipTypeId: RelationshipTypeEnum.HUSBAND.value()],
        ]
        DescriptionInfo descriptionInfo
        AllowanceType dataInstance
        firmList.each { Firm firm ->
            dataList?.each { record ->
                descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record.latinName)
                int count = AllowanceType.countById(firm.code + "-" + record.id)

                if (count == 0) {
                    dataInstance = new AllowanceType(id: record?.id, descriptionInfo: descriptionInfo, firm: firm, relationshipTypeId: record?.relationshipTypeId).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${dataInstance?.descriptionInfo} allowance type for firm ${firm?.name}."
                }
            }
        }
    }

    /**
     * create firm setting for absence max allowed value:
     */
    void createFirmSetting() {

        List<Firm> firmList = Firm.list()
        def dataList = [
                [propertyName: EnumFirmSetting.MAX_ABSENCE_DAYS.value, propertyValue: '30'],
                [propertyName: EnumFirmSetting.REPORT_LOGO_IMAGE_NAME.value, propertyValue: 'logo.jpg'],
                [propertyName: EnumFirmSetting.REPORT_GREEN_IMAGE_NAME.value, propertyValue: 'green.jpg'],
                [propertyName: EnumFirmSetting.REPORT_MINISTRY_IMAGE_NAME.value, propertyValue: 'ministry.jpg'],
                [propertyName: EnumFirmSetting.HEADER_ARABIC_COUNTRY_VALUE.value, propertyValue: "دولـة فلســطين"],
                [propertyName: EnumFirmSetting.HEADER_ENGLISH_COUNTRY_VALUE.value, propertyValue: "State of Palestine"],
                [propertyName: EnumFirmSetting.HEADER_ENGLISH_MINISTRY_VALUE.value, propertyValue: "Ministry of Interior"],
                [propertyName: EnumFirmSetting.HEADER_ARABIC_MINISTRY_VALUE.value, propertyValue: "وزارة الداخلية"],
                [propertyName: EnumFirmSetting.HEADER_ARABIC_ORGANIZATION_VALUE.value],
                [propertyName: EnumFirmSetting.HEADER_ENGLISH_ORGANIZATION_VALUE.value],
                [propertyName: EnumFirmSetting.USER_NAME_LABEL_VALUE.value, propertyValue: 'اسم المستخدم'],
                [propertyName: EnumFirmSetting.CENTRALIZED_WITH_AOC.value, propertyValue: 'true'],
        ]
        FirmSetting dataInstance

        firmList.each { Firm firm ->

            dataList?.each { record ->
                dataInstance = FirmSetting.createCriteria().get {
                    eq('propertyName', record?.propertyName)
                    eq('firm.id', firm?.id)
                }
                if (!dataInstance) {
                    dataInstance = new FirmSetting()
                    if (record?.propertyName == EnumFirmSetting.HEADER_ARABIC_ORGANIZATION_VALUE.value) {
                        dataInstance.propertyValue = firm?.name
                    } else if (record?.propertyName == EnumFirmSetting.HEADER_ENGLISH_ORGANIZATION_VALUE.value) {
                        dataInstance?.propertyValue = firm?.code
                    } else {
                        dataInstance.propertyValue = record?.propertyValue
                    }
                    dataInstance.propertyName = record?.propertyName
                    dataInstance.firm = firm
                    dataInstance.save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${dataInstance?.propertyName} firm setting for firm ${firm?.name}."
                }
            }
        }
    }

    /**
     * create absence reason as default disciplinary reason:
     */
    void createDisciplinaryReason() {
        List<Firm> firmList = Firm.list()
        def dataListDisciplinaryReason = [
                [id: 1, localName: "غياب", latinName: 'ABSENCE_REASON'],
        ]

        def dataListDisciplinaryCategory = [
                [id: 1, localName: "انضباطي", latinName: 'DISCIPLINARY'],
        ]

        DescriptionInfo descriptionInfo1
        DescriptionInfo descriptionInfo2
        DisciplinaryReason disciplinaryReason
        DisciplinaryCategory disciplinaryCategory

        firmList.each { Firm firm ->
            dataListDisciplinaryCategory?.each { record1 ->
                descriptionInfo1 = new DescriptionInfo(localName: record1.localName, latinName: record1?.latinName)
                disciplinaryCategory = DisciplinaryCategory.createCriteria().get {
                    eq('latinName', descriptionInfo1?.latinName)
                    eq('firm.id', firm?.id)
                }
                if (!disciplinaryCategory) {
                    disciplinaryCategory = new DisciplinaryCategory(id: record1?.id, descriptionInfo: descriptionInfo1, firm: firm).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${disciplinaryCategory?.descriptionInfo} disciplinary category for firm ${firm?.name}."
                }

                dataListDisciplinaryReason?.each { record2 ->
                    descriptionInfo2 = new DescriptionInfo(localName: record2.localName, latinName: record2?.latinName)
                    disciplinaryReason = DisciplinaryReason.createCriteria().get {
                        eq('latinName', descriptionInfo2?.latinName)
                        eq('firm.id', firm?.id)
                    }
                    if (!disciplinaryReason) {
                        disciplinaryReason = new DisciplinaryReason(id: record2?.id, descriptionInfo: descriptionInfo2, firm: firm, disciplinaryCategories: disciplinaryCategory).save(failOnError: true, flush: true)
                        println "Fresh Database. Creating ${disciplinaryReason?.descriptionInfo} disciplinary reason for firm ${firm?.name}."
                    }
                }
            }
        }
    }

    /**
     * create operation workflow setting
     */
    void createOperationWorkflowSetting() {
        MessageSource messageSourceLocal = formatService.messageSource

        List<EnumWorkFlowOperation> enumWorkFlowOperationList = EnumWorkFlowOperation.values() - [EnumWorkFlowOperation.DEFAULT_NEED_AOC_APPROVAL, EnumWorkFlowOperation.DEFAULT_DOES_NOT_NEED_AOC_APPROVAL]


        def operationWorkflowSettingList = enumWorkFlowOperationList?.collect {
            [it.name(), it.value, it.requestType]
        }

        List<OperationWorkflowSetting> domainList = null
        OperationWorkflowSetting dataInstance
        DescriptionInfo descriptionInfo

        operationWorkflowSettingList?.each { List<String, String> domain ->
            /**
             * check if setting exist
             */
            domainList = OperationWorkflowSetting.createCriteria().list {
                eq('domain', "${domain[1]}")
            }
            /**
             * create setting if not exist
             */
            if (!domainList) {
                if (domain[0] == "PROMOTION_REQUEST") {
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.PERIOD_SETTLEMENT_CURRENT_ARREST", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.PERIOD_SETTLEMENT_CURRENT_ARREST.toString()).save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD.toString()).save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST.toString()).save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.PERIOD_SETTLEMENT", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.PERIOD_SETTLEMENT.toString()).save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.SITUATION_SETTLEMENT", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.SITUATION_SETTLEMENT.toString()).save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.EXCEPTIONAL_REQUEST", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.EXCEPTIONAL_REQUEST.toString()).save(failOnError: true, flush: true)
                } else if (domain[0] == "EMPLOYMENT_SERVICE_REQUEST") {
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.RETURN_TO_SERVICE", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.RETURN_TO_SERVICE.toString()).save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSetting(descriptionInfo: new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumRequestType.END_OF_SERVICE", null, arabicLocal) + "/" + messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal), latinName: "${domain[1]}"), domain: "${domain[1]}", requestType: EnumRequestType.END_OF_SERVICE.toString()).save(failOnError: true, flush: true)
                } else {
                    descriptionInfo = new DescriptionInfo(localName: messageSourceLocal.getMessage("EnumWorkFlowOperation." + "${domain[0]}", null, arabicLocal)
                            , latinName: "${domain[1]}")
                    if (domain[2] != null) {
                        dataInstance = new OperationWorkflowSetting(descriptionInfo: descriptionInfo, domain: "${domain[1]}", requestType: "${domain[2]}").save(failOnError: true, flush: true)
                    } else {
                        dataInstance = new OperationWorkflowSetting(descriptionInfo: descriptionInfo, domain: "${domain[1]}").save(failOnError: true, flush: true)
                    }
                }
                println "Fresh Database, creating ${"${domain[1]}"}'s operation workflow setting."

            };
        }
    }

    /**
     * create operation workflow setting param
     */
    void createOperationWorkflowSettingParam() {

        List customWorkFlowApprovedList = [EnumWorkFlowOperation.DEFAULT_DOES_NOT_NEED_AOC_APPROVAL.getValue(), EnumWorkFlowOperation.DISCIPLINARY_REQUEST.getValue()]

        OperationWorkflowSettingParam dataInstance

        List<OperationWorkflowSettingParam> operationWorkflowSettingParamList = null

        /**
         * get list of operation workflow Settings
         */
        List operationWorkflowSettingList = OperationWorkflowSetting.list()


        operationWorkflowSettingList?.each { OperationWorkflowSetting operationWorkflowSetting ->

            /**
             * check if setting exist
             */
            operationWorkflowSettingParamList = OperationWorkflowSettingParam.createCriteria().list {
                eq('operationWorkflowSetting.id', operationWorkflowSetting?.id)
            }

            /**
             * create setting params  if not exist
             */
            if (!operationWorkflowSettingParamList) {

                if (operationWorkflowSetting.domain.equals(EnumWorkFlowOperation.AOC_CORRESPONDENCE_LIST.value)) {
                    dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "currentStatus", paramValue: "IN_PROGRESS", workflowSettingType: "START_WORK_FLOW").save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "currentStatus", paramValue: "REJECTED", workflowSettingType: "REJECT_WORK_FLOW").save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "currentStatus", paramValue: "APPROVED", workflowSettingType: "END_WORK_FLOW").save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "currentStatus", paramValue: "STOPPED", workflowSettingType: "REQUEST_NOT_RELATED_TO_WORKFLOW").save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "currentStatus", paramValue: "STOPPED", workflowSettingType: "REQUEST_EMPLOYMENT_NOT_RELATED_TO_WORKFLOW").save(failOnError: true, flush: true)
                } else {
                    dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "IN_PROGRESS", workflowSettingType: "START_WORK_FLOW").save(failOnError: true, flush: true)
                    dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "REJECTED", workflowSettingType: "REJECT_WORK_FLOW").save(failOnError: true, flush: true)

                    if (operationWorkflowSetting.domain in customWorkFlowApprovedList) {
                        dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "APPROVED", workflowSettingType: "END_WORK_FLOW").save(failOnError: true, flush: true)
                        dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "APPROVED", workflowSettingType: "REQUEST_NOT_RELATED_TO_WORKFLOW").save(failOnError: true, flush: true)
                        dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "APPROVED", workflowSettingType: "REQUEST_EMPLOYMENT_NOT_RELATED_TO_WORKFLOW").save(failOnError: true, flush: true)
                    } else {
                        dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "APPROVED_BY_WORKFLOW", workflowSettingType: "END_WORK_FLOW").save(failOnError: true, flush: true)
                        dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "APPROVED_BY_WORKFLOW", workflowSettingType: "REQUEST_NOT_RELATED_TO_WORKFLOW").save(failOnError: true, flush: true)
                        dataInstance = new OperationWorkflowSettingParam(operationWorkflowSetting: operationWorkflowSetting, paramName: "requestStatus", paramValue: "APPROVED_BY_WORKFLOW", workflowSettingType: "REQUEST_EMPLOYMENT_NOT_RELATED_TO_WORKFLOW").save(failOnError: true, flush: true)
                    }
                }

                println "Fresh Database, creating ${"${operationWorkflowSetting?.domain}"}'s operation workflow setting param."
            }

        }
    }


    private void resetBelowCounter(Map counters, MenuLevel menuLevel) {
        if (menuLevel == MenuLevel.PARENT) {
            counters[MenuLevel.SUB_PARENT] = 0
            counters[MenuLevel.SUB_PARENT_1] = 0
            counters[MenuLevel.SUB_PARENT_2] = 0
            counters[MenuLevel.SUB_PARENT_3] = 0
            counters[MenuLevel.SUB_PARENT_4] = 0
        } else if (menuLevel == MenuLevel.SUB_PARENT) {
            counters[MenuLevel.SUB_PARENT_1] = 0
            counters[MenuLevel.SUB_PARENT_2] = 0
            counters[MenuLevel.SUB_PARENT_3] = 0
            counters[MenuLevel.SUB_PARENT_4] = 0
        } else if (menuLevel == MenuLevel.SUB_PARENT_1) {
            counters[MenuLevel.SUB_PARENT_2] = 0
            counters[MenuLevel.SUB_PARENT_3] = 0
            counters[MenuLevel.SUB_PARENT_4] = 0
        } else if (menuLevel == MenuLevel.SUB_PARENT_2) {
            counters[MenuLevel.SUB_PARENT_3] = 0
            counters[MenuLevel.SUB_PARENT_4] = 0
        } else if (menuLevel == MenuLevel.SUB_PARENT_3) {
            counters[MenuLevel.SUB_PARENT_4] = 0
        } else {
            counters = [:];
        }
    }

    public String generateNumber(int currentNumber, int length) {
        String number = ""
        for (int i = 0; i < length - (currentNumber.toString().length()); i++) {
            number += "0"
        }
        number += currentNumber.toString();
        return number
    }

    def getImageData(String recordId, String firmDocumentLatinName, javax.servlet.http.HttpServletRequest request) {
        try {
            FirmDocument firmDocument = FirmDocument.createCriteria().get {
                eq('latinName', firmDocumentLatinName)
                eq('firm.id', PCPSessionUtils.getValue("firmId"))
                maxResults(1)
            }
            List<AttachmentFile> attachmentFiles = attachmentService.searchAttachment(new GrailsParameterMap([parentId_SearchMH: recordId, attachmentType_SearchM: firmDocument?.id], request), false)
            if (attachmentFiles) {
                AttachmentFile attachmentFile = attachmentFiles?.sort { it.trackingInfo.dateCreatedUTC }?.last()
                NodeDataBean nodeDataBean = attachmentManageService.getFile(attachmentFile?.nodeCode, true)
                if (nodeDataBean != null && nodeDataBean.errorList?.size() == 0 && nodeDataBean.fileByts?.length > 0) {
                    return nodeDataBean
                }
                return null
            }
        }
        catch (Exception e) {
            println("can't connect to attachment plugin")
        }
        return null
    }


    Map getAttachmentTypeListAsMap(String referenceObject, EnumOperation enumOperation, EnumOperation sharedOperationType = null) {
        List attachmentTypeList = joinedFirmOperationDocumentService.getAttachmentType(enumOperation)
        return [attachmentTypeList: (attachmentTypeList as JSON)?.toString(), sharedOperationType: sharedOperationType, operationType: enumOperation, referenceObject: referenceObject]
    }

    /*
       * this method is used to create default inspection category called 'فحص لياقة بدنية'
       */

    void createInspectionCategory() {

        /*to get list of firms in system*/
        List<Firm> firmList = Firm.list()

        def dataList = [
                [id: 1, localName: "فحص لياقة بدنية", latinName: 'physical inspection'],
        ]
        DescriptionInfo descriptionInfo
        InspectionCategory dataInstance

        firmList.each { Firm firm ->

            dataList?.each { record ->
                descriptionInfo = new DescriptionInfo(localName: record.localName, latinName: record?.latinName)
                dataInstance = InspectionCategory.createCriteria().get {
                    eq('latinName', descriptionInfo?.latinName)
                    eq('firm.id', firm?.id)
                }
                if (!dataInstance) {
                    dataInstance = new InspectionCategory(id: record?.id, descriptionInfo: descriptionInfo, orderId: 1, hasMark: true, isRequiredByFirmPolicy: true, hasResultRate: true, firm: firm).save(failOnError: true, flush: true)
                    println "Fresh Database. Creating ${dataInstance?.descriptionInfo} inspection category for firm ${firm?.name}."
                }
            }
        }
    }

    /**
     * generate the code for list.
     * @param domainClassName
     * @return the new max sequence as Integer
     */
    public String generateListCode(String domainClassName, String source, int stringLength) {
        int length = stringLength - (source.length());
        GrailsClass domainClassInfo = grailsApplication.getArtefact("Domain", domainClassName)
        def domainClass = domainClassInfo?.clazz
        //TODO: the code should be taken per year and firm
        int maxNumber = domainClass.createCriteria().count { projections { max('code') }; }
        def number = source;
        if (maxNumber) {
            maxNumber++;
        } else {
            maxNumber = 1;
        }
        for (int i = 0; i < length - (maxNumber.toString().length()); i++) {
            number += "0"
        }

        number += maxNumber.toString();
        return number
    }

    /**
     * generate the code for list.
     * @param String encodedId
     * @param String entityName
     * @param Object domainClass
     * @param Boolean isAttributeInRequest
     * @param String attributeName
     * @return Map
     */
    Map goToList(String encodedId, String entityName, Object domainClass, Boolean isAttributeInRequest = false, String attributeName = null, Boolean listLinkWithEntityName = false) {
        String id = HashHelper.decode(encodedId)
        String capitalEntityName = entityName.capitalize()
        Object listInstance
        if (!attributeName) {
            attributeName = "${entityName}Request"
        }
        if (isAttributeInRequest) {
            listInstance = domainClass.load(id)?."${entityName}ListEmployee"?."${entityName}List"
        } else {
            listInstance = domainClass.createCriteria().get {
                eq("${attributeName}.id", id)
            }?."${entityName}List"
        }
        if (listInstance) {
            String linkName = "manageList"
            if (!listLinkWithEntityName) {
                linkName = "manage${capitalEntityName}List"
            }
            def link = [controller: "${entityName}List", action: linkName, params: [encodedId: "${listInstance?.encodedId}"]]
            return link
        }
        return [:]
    }
}
