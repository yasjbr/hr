<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'employee.entity', default: 'Organization List')}"/>
    <g:set var="tabEntity" value="${message(code: 'trainingRecord.entity', default: 'trainingRecord')}"/>
    <g:set var="tabEntities" value="${message(code: 'trainingRecord.entities', default: 'trainingRecord')}"/>
    <g:set var="tabList"
           value="${message(code: 'default.list.label', args: [tabEntities], default: 'list trainingRecord')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create trainingRecord')}"/>

    <el:form action="#" style="display: none;" name="trainingRecordSearchForm">
        <el:hiddenField name="employee.id" value="${entityId}"/>
        <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS"/>
    </el:form>
    <g:render template="/trainingRecord/dataTable"
              model="[isInLineActions: true, title: tabList, entity: tabEntity, domainColumns: 'DOMAIN_TAB_COLUMNS']"/>

    <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
        <g:if test="${isSync == "false"}">
            <div class="clearfix form-actions text-center">
                <btn:createButton class="btn btn-sm btn-info2"
                                  accessUrl="${createLink(controller: tabEntityName, action: 'create')}"
                                  onclick="renderInLineCreate()"
                                  label="${tabTitle}">
                    <i class="icon-plus"></i>
                </btn:createButton>
            </div>
        </g:if>
    </sec:ifAnyGranted>


    <sec:ifNotGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
        <div class="clearfix form-actions text-center">
            <btn:createButton class="btn btn-sm btn-info2"
                              accessUrl="${createLink(controller: tabEntityName, action: 'create')}"
                              onclick="renderInLineCreate()"
                              label="${tabTitle}">
                <i class="icon-plus"></i>
            </btn:createButton>
        </div>
    </sec:ifNotGranted>

</div>