<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'department.entity', default: 'department List')}" />
    <g:set var="tabEntity" value="${message(code: 'employee.entity', default: 'employee')}" />
    <g:set var="tabEntities" value="${message(code: 'employee.entities', default: 'employees')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list Phone')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create Phone')}" />

    <lay:collapseWidget id="employeeCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [tabEntities])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="employeeSearchForm">
                <el:hiddenField name="department.id" value="${entityId}" />
                <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS"/>


                <el:formGroup>
                    <el:autocomplete
                            optionKey="id"
                            optionValue="name"
                            size="6"
                            class=" "
                            controller="person"
                            action="autocomplete"
                            name="personId"
                            label="${message(code: 'employee.transientData.personDTO.localFullName.label', default: 'employee name')}"/>
                    <el:textField name="militaryNumber" size="6" class=""
                                  label="${message(code: 'employee.militaryNumber.label', default: 'militaryNumber')}"/>
                </el:formGroup>

                <el:formGroup>
                    <el:autocomplete
                            optionKey="id"
                            optionValue="name"
                            size="6"
                            class=" "
                            controller="militaryRank"
                            action="autocomplete"
                            name="militaryRank.id"
                            label="${message(code: 'employee.currentEmployeeMilitaryRank.militaryRank.descriptionInfo.localName.label', default: 'militaryRank')}"/>
                    <el:autocomplete
                            optionKey="id"
                            optionValue="name"
                            size="6"
                            class=" "
                            controller="militaryRankType"
                            action="autocomplete"
                            name="militaryRankType.id"
                            label="${message(code: 'militaryRankType.label', default: 'militaryRankType')}"/>
                </el:formGroup>


                <el:formGroup>
                    <el:autocomplete
                            optionKey="id"
                            optionValue="name"
                            size="6"
                            class=" "
                            controller="militaryRankClassification"
                            action="autocomplete"
                            name="militaryRankClassification.id"
                            label="${message(code: 'militaryRankClassification.label', default: 'militaryRankClassification')}"/>

                    <el:textField name="financialNumber" size="6" class=""
                                  label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
                </el:formGroup>

                <el:formGroup>
                    <el:textField name="recentCardNo" size="6" class=""
                                  label="${message(code: 'person.recentCardNo.label', default: 'recentCardNo')}"/>


                    <el:autocomplete optionKey="id" optionValue="name" size="6"
                                     class=""
                                     controller="employeeStatusCategory" action="autocomplete"
                                     name="categoryStatusId" label="${message(code:'employee.categoryStatus.label',default:'categoryStatus')}" />
                </el:formGroup>


                <el:formButton functionName="search" onClick="_dataTables['employeeTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('employeeSearchForm');_dataTables['employeeTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <g:render template="/employee/dataTable"
              model="[isInLineActions:true,title:tabList,entity:tabEntity,domainColumns:'DOMAIN_TAB_COLUMNS',
                      isReadOnly:isReadOnly,preventDataTableTools:preventDataTableTools]"/>

</div>