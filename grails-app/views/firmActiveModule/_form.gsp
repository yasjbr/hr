<el:formGroup>
    <el:select valueMessagePrefix="EnumSystemModule"  from="${ps.gov.epsilon.hr.enums.v1.EnumSystemModule.values()-selectedSystemModuleList}" name="systemModule" size="8"  class=" isRequired" label="${message(code:'firmActiveModule.systemModule.label',default:'systemModule')}" value="${firmActiveModule?.systemModule}" />
</el:formGroup>

