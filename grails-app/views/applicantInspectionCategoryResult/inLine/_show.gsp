<g:render template="/applicantInspectionCategoryResult/show" model="[applicantInspectionCategoryResult:applicantInspectionCategoryResult]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${applicantInspectionCategoryResult?.encodedId}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>