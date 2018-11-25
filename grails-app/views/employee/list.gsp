<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'employee.entities', default: 'Employee List')}"/>
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'Employee')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'Employee List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="employeeCollapseWidget" icon="icon-search"
                    title="${message(code: 'employee.search.label')}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller: 'employee', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <cache:advanceSearchForm action="#" name="employeeSearchForm" dataTableName="employeeTable"
                                 withAdvanceOrder="true" withDataTableControl="true"
                                 saveDataTableControlInDataBase="true" isStringId="true"
                                 searchDomain="ps.gov.epsilon.hr.firm.profile.Employee" domainColumnsName="DOMAIN_COLUMNS_DT_CONTROL"
                                 anotherDomains="[
                                         [name:'ps.gov.epsilon.hr.firm.profile.EmploymentRecord',prefix:'currentEmploymentRecord'],
                                         [name:'ps.gov.epsilon.hr.firm.promotion.EmployeePromotion',prefix:'currentEmployeeMilitaryRank'],
                                         [name:'ps.gov.epsilon.hr.firm.lookups.JobTitle',prefix:'currentEmploymentRecord.jobTitle'],
                                         [name:'ps.gov.epsilon.hr.firm.lookups.Province',prefix:'currentEmploymentRecord.province'],
                                         [name:'ps.gov.epsilon.hr.firm.lookups.EmploymentCategory',prefix:'currentEmploymentRecord.employmentCategory'],
                                         [name:'ps.gov.epsilon.hr.firm.Department',prefix:'currentEmploymentRecord.department'],
                                         [name:'ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory',prefix:'categoryStatus',],
                                 ]"
                                 remotingSearch="${[
                                         [
                                                 serviceName:'ps.police.pcore.v2.entity.person.PersonService',
                                                 methodName:'searchPerson',prefix:'person',fieldInDomain:'personId',
                                                 fields:[[name:'localFullName',fullType:String.class.name,type:'string'],[name:'recentCardNo',fullType:String.class.name,type:'string']]
                                         ],
                                         [
                                                 serviceName:'ps.police.pcore.v2.entity.location.lookups.GovernorateService',
                                                 controllerName:'governorate',
                                                 methodName:'searchGovernorate',message:'location.governorate.label',fieldInDomain:'currentEmploymentRecord.department.governorateId',
                                                 isDomain:true,
                                                 fields:[[name:'id',fullType:List.class.name,type:'long']]
                                         ]
                                 ]}"
                                 excludeFields="[
                                         'bankBranchId','currentEmployeeMilitaryRank','currentEmploymentRecord',
                                         'currentEmploymentRecord.firm','currentEmploymentRecord.note','currentEmployeeMilitaryRank.firm',
                                         'currentEmployeeMilitaryRank.note','currentEmployeeMilitaryRank.militaryRankTypeDate',
                                         'currentEmployeeMilitaryRank.promotionListEmployee','currentEmployeeMilitaryRank.transientData',
                                         'currentEmploymentRecord.internalOrderDate','currentEmploymentRecord.internalOrderNumber',
                                         'currentEmploymentRecord.jobTitle.firm','currentEmploymentRecord.jobTitle.allowToRepeetInUnit',
                                         'currentEmploymentRecord.jobTitle.allowToRepeetInUnit','currentEmploymentRecord.jobTitle.jobCategory',
                                         'currentEmploymentRecord.jobTitle.note','currentEmploymentRecord.jobTitle.universalCode',
                                         'currentEmploymentRecord.province.universalCode','currentEmploymentRecord.province.note',
                                         'currentEmploymentRecord.employmentCategory.universalCode' ,'currentEmploymentRecord.employmentCategory.firm',
                                         'currentEmploymentRecord.department.departmentType','currentEmploymentRecord.department.functionalParentDeptId',
                                         'currentEmploymentRecord.department.locationId','currentEmploymentRecord.department.managerialParentDeptId',
                                         'currentEmploymentRecord.department.governorateId','currentEmploymentRecord.department.note',
                                         'currentEmploymentRecord.department.firm','currentEmploymentRecord.department.unstructuredLocation',
                                         'categoryStatus.description','categoryStatus.firm','categoryStatus.universalCode',
                                 ]">
            <g:render template="/employee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.viewCache.resetCacheForm('employeeSearchForm');_dataTables['employeeTable'].draw();"/>
        </cache:advanceSearchForm>
    </lay:widgetBody>
</lay:collapseWidget>
<report:list viewInList="true" withDataTable="employeeTable" searchFromName="employeeSearchForm" columns="DOMAIN_COLUMNS_DT_CONTROL"
             domain="employee" method="searchWithRemotingValues" withModal="true" withChangeReportTitle="true"
             format="pdf, html, xml, csv, xls, rtf, text,odt,ods,docx,xlsx,pptx" />
<g:render template="/employee/dataTable" model="[domainColumns:'DOMAIN_COLUMNS']" />