<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'personArrestHistory.entity', default: 'personArrestHistory')}" />
    <g:set var="tabEntities" value="${message(code: 'personArrestHistory.entities', default: 'personArrestHistory')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list personArrestHistory')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create personArrestHistory')}" />


    <el:form action="#" style="display: none;" name="personArrestHistorySearchForm">
        <el:hiddenField name="id" value="${entityId}" />
    </el:form>
    <g:render template="/pcore/person/personArrestHistory/dataTable"
              model="[isInLineActions:true,
                      title:tabList,
                      entity:entity,
                      domainColumns:'ARREST_HISTORY_TAB_COLUMNS',
                      controller: 'applicant',
                      filterName:'filterPersonArrestHistory',
                      serviceName:'applicant']"/>
    <div class="clearfix form-actions text-center">
        <btn:createButton class="btn btn-sm btn-info2"
                          accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineContactInfoCreate()"
                          label="${tabTitle}">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>
</div>

