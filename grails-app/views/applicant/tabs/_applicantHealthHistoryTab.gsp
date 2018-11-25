--------------------------------

<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'personHealthHistory.entity', default: 'personHealthHistory')}" />
    <g:set var="tabEntities" value="${message(code: 'personHealthHistory.entities', default: 'personHealthHistory')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list personHealthHistory')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create personHealthHistory')}" />


    <el:form action="#" style="display: none;" name="personHealthHistorySearchForm">
        <el:hiddenField name="id" value="${entityId}" />
    </el:form>
    <g:render template="/pcore/person/personHealthHistory/dataTable"
              model="[isInLineActions:true,
                      title:tabList,
                      entity:entity,
                      domainColumns:'HEALTH_HISTORY_TAB_COLUMNS',
                      controller: 'applicant',
                      filterName:'filterPersonHealthHistory',
                      serviceName:'applicant']"/>
    <div class="clearfix form-actions text-center">
        <btn:createButton class="btn btn-sm btn-info2"
                          accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineContactInfoCreate()"
                          label="${tabTitle}">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>
</div>

