<g:render template="/applicantContactInfo/show" model="[applicantContactInfo:applicantContactInfo]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineContactInfoEdit('${applicantContactInfo?.id}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>