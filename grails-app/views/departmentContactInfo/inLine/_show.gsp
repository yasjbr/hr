<g:render template="/departmentContactInfo/show" model="[departmentContactInfo:departmentContactInfo]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <g:if test="${!isReadOnly && !params['isReadOnly']}">
        <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${departmentContactInfo?.id}')"/>
    </g:if>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>