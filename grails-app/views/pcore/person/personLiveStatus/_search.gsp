<g:render template="/pcore/person/wrapper" model="[bean:personLiveStatus?.person,isSearch:true]" />
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" " label="${message(code:'personLiveStatus.fromDate.label',default:'fromDate')}" value="${personLiveStatus?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" " label="${message(code:'personLiveStatus.toDate.label',default:'toDate')}" value="${personLiveStatus?.toDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="liveStatus" action="autocomplete" name="liveStatus.id" label="${message(code:'personLiveStatus.liveStatus.label',default:'liveStatus')}" values="${[[personLiveStatus?.liveStatus?.id,personLiveStatus?.liveStatus?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personLiveStatus.note.label',default:'note')}" value="${personLiveStatus?.note}"/>
</el:formGroup>