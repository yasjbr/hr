<div id="${tabEntityName}Div">
<g:set var="entity" value="${message(code: 'organization.entity', default: 'Organization List')}" />
<g:set var="tabEntity" value="${message(code: 'legalIdentifier.entity', default: 'legalIdentifier')}" />
<g:set var="tabEntities" value="${message(code: 'legalIdentifier.entities', default: 'legalIdentifiers')}" />
<g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'organization legalIdentifiers')}" />
<g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'legalIdentifier List')}" />

<el:form action="#" style="display: none;" name="legalIdentifierSearchForm">
    <el:hiddenField name="ownerOrganization.id" value="${entityId}" />
</el:form>
<g:render template="/pcore/person//legalIdentifier/dataTable"
          model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'DOMAIN_TAB_COLUMNS']"/>
<div class="clearfix form-actions text-center">
    <btn:createButton class="btn btn-sm btn-info2"
                      accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineCreate()"
                      label="${tabTitle}">
        <i class="icon-plus"></i>
    </btn:createButton>
</div>
</div>