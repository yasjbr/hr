<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant List')}" />
    <g:set var="tabEntity" value="${message(code: 'applicantEducation.entity', default: 'applicantEducation')}" />
    <g:set var="tabEntities" value="${message(code: 'applicantEducation.entities', default: 'applicantEducation')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list applicantEducation')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create applicantEducation')}" />


    <el:form action="#" style="display: none;" name="applicantEducationSearchForm">
        <el:hiddenField name="id" value="${entityId}" />
    </el:form>
    <g:render template="/applicantEducation/dataTable"
              model="[isInLineActions:true,title:tabList,entity:entity,domainColumns:'EDUCATION_COLUMNS']"/>
    <div class="clearfix form-actions text-center">
        <btn:createButton class="btn btn-sm btn-info2"
                          accessUrl="${createLink(controller: tabEntityName,action: 'create')}" onclick="renderInLineApplicantEducationCreate()"
                          label="${tabTitle}">
            <i class="icon-plus"></i>
        </btn:createButton>
    </div>
</div>