%{--<g:render template="/person/wrapper" model="[bean:personCountryVisit?.person]" />--}%

<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                                   bean:personCountryVisit?.person,
                                             isDisabled:isPersonDisabled]" />

<el:formGroup>
    <el:textField name="reasonForVisit" size="8"  class=" isRequired" label="${message(code:'personCountryVisit.reasonForVisit.label',default:'reasonForVisit')}" value="${personCountryVisit?.reasonForVisit}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="startVisitDate"  size="8" class=" isRequired" label="${message(code:'personCountryVisit.startVisitDate.label',default:'startVisitDate')}" value="${personCountryVisit?.startVisitDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="endVisitDate"  size="8" class=" " label="${message(code:'personCountryVisit.endVisitDate.label',default:'endVisitDate')}" value="${personCountryVisit?.endVisitDate}" />
</el:formGroup>


<lay:wall title="${g.message(code: 'personCountryVisit.location.label')}">
    <g:render template="/pcore/location/wrapper"
              model="[
                      isCountryRequired         : true,
                      hiddenDetails             : true,
                      size                      : 8,
                      location:personCountryVisit?.location]" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" value="${personCountryVisit?.unstructuredLocation}"/>
    </el:formGroup>
</lay:wall>