<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>

%{--
<g:render template="/DescriptionInfo/wrapper" model="[bean:militaryRank?.descriptionInfo,isSearch:true]" />
--}%
<el:formGroup>
    <el:textField name="descriptionInfo.localName" size="8" class=""
                  label="${messageValue?:(message(code:'descriptionInfo.localName.label',default:'localName'))}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="orderNo" size="8" maxlength="2" class="isNumber" label="${message(code:'militaryRank.orderNo.label',default:'orderNo')}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="numberOfYearToPromote" size="8"  class=" isNumber" label="${message(code:'militaryRank.numberOfYearToPromote.label',default:'numberOfYearToPromote')}" />
</el:formGroup>
