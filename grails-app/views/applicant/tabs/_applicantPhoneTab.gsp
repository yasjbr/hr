<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'phone.entity', default: 'applicantContactInfo')}" />
    <g:set var="tabEntities" value="${message(code: 'phone.entities', default: 'applicantContactInfo')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list applicantContactInfo')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create applicantPhone')}" />


    <el:form action="#" style="display: none;" name="applicantContactInfoSearchForm">
        <el:hiddenField name="id" value="${entityId}" />
        <el:hiddenField name="ContactMethodEnum" value="${[ps.police.pcore.enums.v1.ContactMethodEnum.PHONE_NUMBER.value(), ps.police.pcore.enums.v1.ContactMethodEnum.MOBILE_NUMBER.value(),ps.police.pcore.enums.v1.ContactMethodEnum.EMAIL.value()]}" />
    </el:form>
    <g:render template="/applicantContactInfo/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'CONTACT_INFO_COLUMNS']"/>
    <div class="clearfix form-actions text-center">
        <btn:createButton class="btn btn-sm btn-info2"
                          accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineContactInfoCreate()"
                          label="${tabTitle}">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>
</div>