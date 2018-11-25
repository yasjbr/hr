<g:render template="/pcore/person/wrapper" model="[bean:personCountryVisit?.person,isSearch:true]" />
<el:formGroup>
    <el:dateField name="endVisitDate"  size="8" class="" label="${message(code:'personCountryVisit.endVisitDate.label',default:'endVisitDate')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="reasonForVisit" size="8"  class="" label="${message(code:'personCountryVisit.reasonForVisit.label',default:'reasonForVisit')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="startVisitDate"  size="8" class="" label="${message(code:'personCountryVisit.startVisitDate.label',default:'startVisitDate')}"  />
</el:formGroup>

<lay:wall title="${g.message(code: "location.label")}">
        <g:render template="/pcore/location/searchWrapper" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}"/>
    </el:formGroup>
</lay:wall>




