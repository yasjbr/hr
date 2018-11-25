<g:render template="/pcore/person/wrapper" model="[bean:personArrestHistory?.person,isSearch:true]" />
<el:formGroup>
    <el:textField name="accusation" size="8"  class=" " label="${message(code:'personArrestHistory.accusation.label',default:'accusation')}" value="${personArrestHistory?.accusation}"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" name="arrestDate" size="8" class=""
              label="${message(code:'personArrestHistory.arrestDate.label',default:'arrestDate')}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="arrestReason" size="8"  class="" label="${message(code:'personArrestHistory.arrestReason.label',default:'arrestReason')}" value="${personArrestHistory?.arrestReason}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="ArrestingClassification" from="${ps.police.pcore.enums.v1.ArrestingClassification.values()}" name="arrestingClassification" size="8"  class=" " label="${message(code:'personArrestHistory.arrestingClassification.label',default:'arrestingClassification')}" value="${personArrestHistory?.arrestingClassification}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="ArrestingParty" from="${ps.police.pcore.enums.v1.ArrestingParty.values()}" name="arrestingParty" size="8"  class=" " label="${message(code:'personArrestHistory.arrestingParty.label',default:'arrestingParty')}" value="${personArrestHistory?.arrestingParty}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="jail" action="autocomplete" name="jail.id" label="${message(code:'personArrestHistory.jail.label',default:'jail')}" values="${[[personArrestHistory?.jail?.id,personArrestHistory?.jail?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="jailName" size="8"  class="" label="${message(code:'personArrestHistory.jailName.label',default:'jailName')}" value="${personArrestHistory?.jailName}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="lawyerName" size="8"  class="" label="${message(code:'personArrestHistory.lawyerName.label',default:'lawyerName')}" value="${personArrestHistory?.lawyerName}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personArrestHistory.note.label',default:'note')}" value="${personArrestHistory?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:range type="date" name="releaseDate" size="8" class=""
              label="${message(code:'personArrestHistory.releaseDate.label',default:'releaseDate')}" />
</el:formGroup>
<el:formGroup>
    <el:select name="isJudgementForEver" size="8" class=""
               label="${message(code: 'personArrestHistory.isJudgementForEver.label', default: 'isJudgementForEver')}"
               from="['','true','false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"/>
</el:formGroup>