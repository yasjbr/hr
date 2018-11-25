
<g:render template="/pcore/person/contactInfo/show" model="[contactInfo:contactInfo]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${contactInfo?.id}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>