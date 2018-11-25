<g:render template="/pcore/person/personCharacteristics/show" model="[personCharacteristics:personCharacteristics]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${personCharacteristics?.id}')"/>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>