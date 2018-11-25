<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'person.entity', default: 'Person List')}" />
<g:set var="tabEntity" value="${message(code: 'personHealthHistory.entity', default: 'person Health History')}" />
<g:set var="tabEntities" value="${message(code: 'personHealthHistory.entities', default: 'person Health History')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'person Health History List')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'Person List')}" />


<el:form action="#" style="display: none;" name="personHealthHistorySearchForm">
    <el:hiddenField name="person.id" value="${entityId}" />
    <el:hiddenField name="domainColumns" value="DOMAIN_TAB_COLUMNS" />
</el:form>

<g:render template="/pcore/person/personHealthHistory/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>

    <div class="clearfix form-actions text-center">
        <btn:createButton class="btn btn-sm btn-info2"
                          accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineCreate()"
                          label="${tabTitle}">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>
</div>