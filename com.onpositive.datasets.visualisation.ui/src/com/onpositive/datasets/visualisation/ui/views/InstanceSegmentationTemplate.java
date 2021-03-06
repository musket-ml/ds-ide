package com.onpositive.datasets.visualisation.ui.views;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import com.onpositive.mmdetection.wrappers.ExampleExtractor;
import com.onpositive.mmdetection.wrappers.MMDetCfgData;
import com.onpositive.mmdetection.wrappers.ModulePathExtractor;
import com.onpositive.mmdetection.wrappers.ModulePathExtractor.Data;
import com.onpositive.python.command.IPythonPathProvider;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.IValidator;
import com.onpositive.semantic.model.binding.Binding;

@Display("dlf/instanceSegmentationTemplate.dlf")
public class InstanceSegmentationTemplate extends GenericExperimentTemplate {

	
	public InstanceSegmentationTemplate(IPythonPathProvider projectWrapper, IProject prj) {
		this.prj = prj;
		this.projectWrapper = projectWrapper;
		ExampleExtractor ee = new ExampleExtractor();

		Data data = ModulePathExtractor.extractCheckpointsAndConfigs(this.projectWrapper.getPythonPath());
		
		List<MMDetCfgData> builtinConfigs = data.getConfigs();		
		String wsConfigsPath = this.prj.getName() + "/modules";
		List<MMDetCfgData> wsConfigs = ee.processEclipseFile(wsConfigsPath);
		
		ArrayList<MMDetCfgData> configs = new ArrayList<>(builtinConfigs);
		configs.addAll(wsConfigs);		
		this.configs = configs;
		
		this.checkpointNames = data.getCheckpointNames();
		for(MMDetCfgData cfg : this.configs) {
			this.configKeys.addAll(cfg.paramNames());
			this.configsByPath.put(cfg.getPath(), cfg);
		}
		
	}
	
	private List<String> basicPaths = Arrays.asList(new String[] {"faster_rcnn_x101_64x4d_fpn_1x.py", "faster_rcnn_r50_fpn_1x.py", "htc/htc_dconv_c3-c5_mstrain_400_1400_x101_64x4d_fpn_20e.py", "mask_rcnn_x101_64x4d_fpn_1x.py"});
	
	private List<String> basicCaptions = Arrays.asList(new String[] {"Faster RCNN 101", "Faster RCNN 50","Hyper Task Cascade 1", "Mask RCNN" });
	
	private List<MMDetCfgData> configs = null;
	
	private List<MMDetCfgData> pathsList1;
	
	private List<String> checkpointNames;
	
	private Set<String> configKeys = new HashSet<>();
	
	private Map<String,MMDetCfgData> configsByPath = new HashMap<>();
	
	private IProject prj;
	
	private IPythonPathProvider projectWrapper;
	
	@Caption("Architecture")
	protected String architecture;//="HybridTaskCascade";
	
	@Caption("Backbone")
	protected String backbone;//="ResNeXt";
	
	@Caption("Neck")
	protected String neck;
	
	@Caption("RPN head")
	protected String rpn_head;
	
	@Caption("Mask head")
	protected String mask_head;
	
	@Caption("BBox head")
	protected String bbox_head;

	@Caption("Width")
	protected  int width=224;
	@Caption("Height")
	protected  int height=224;
	
	@Caption("Images per GPU")
	protected int imagesPerGpu = 1;
	
	protected MMDetCfgData _selectedConfig;
	
	@Caption("Model weights path")
	@Validator(validatorClass = WeihtsPathValidator.class)
	protected String weightsPath;
	
//	type: MaskScoringRCNN, FCOS, SingleStageDetector, RPN, CascadeRCNN, RetinaNet, MaskRCNN, GridRCNN, FasterRCNN, FastRCNN, HybridTaskCascade
//	path: fp16/mask_rcnn_r50_fpn_fp16_1x.py, rpn_r101_fpn_1x.py, empirical_attention/faster_rcnn_r50_fpn_attention_1111_dcn_1x.py, rpn_x101_64x4d_fpn_1x.py, htc/htc_without_semantic_r50_fpn_1x.py, cascade_rcnn_r101_fpn_1x.py, fast_rcnn_r101_fpn_1x.py, htc/htc_x101_64x4d_fpn_20e_16gpu.py, fcos/fcos_mstrain_640_800_x101_64x4d_fpn_gn_2x.py, ms_rcnn/ms_rcnn_r101_caffe_fpn_1x.py, cascade_mask_rcnn_r50_caffe_c4_1x.py, scratch/scratch_faster_rcnn_r50_fpn_gn_6x.py, fcos/fcos_mstrain_640_800_r101_caffe_fpn_gn_2x_4gpu.py, mask_rcnn_r101_fpn_1x.py, hrnet/cascade_rcnn_hrnetv2p_w32_20e.py, ghm/retinanet_ghm_r50_fpn_1x.py, faster_rcnn_r50_fpn_1x.py, gcnet/mask_rcnn_r16_gcb_c3-c5_r50_fpn_1x.py, dcn/faster_rcnn_mdpool_r50_fpn_1x.py, guided_anchoring/ga_rpn_r101_caffe_rpn_1x.py, htc/htc_dconv_c3-c5_mstrain_400_1400_x101_64x4d_fpn_20e-1.py, gcnet/mask_rcnn_r4_gcb_c3-c5_r50_fpn_1x.py, gn/mask_rcnn_r101_fpn_gn_2x.py, cascade_mask_rcnn_r101_fpn_1x.py, cascade_mask_rcnn_r50_fpn_1x.py, empirical_attention/faster_rcnn_r50_fpn_attention_0010_1x.py, guided_anchoring/ga_faster_x101_32x4d_fpn_1x.py, cascade_mask_rcnn_x101_32x4d_fpn_1x.py, dcn/faster_rcnn_dpool_r50_fpn_1x.py, pascal_voc/faster_rcnn_r50_fpn_1x_voc0712.py, libra_rcnn/libra_fast_rcnn_r50_fpn_1x.py, retinanet_x101_32x4d_fpn_1x.py, libra_rcnn/libra_faster_rcnn_r50_fpn_1x.py, cascade_rcnn_r50_fpn_1x.py, mask_rcnn_x101_64x4d_fpn_1x.py, htc/htc_r50_fpn_1x.py, guided_anchoring/ga_faster_r50_caffe_fpn_1x.py, htc/htc_x101_32x4d_fpn_20e_16gpu.py, cascade_rcnn_r50_caffe_c4_1x.py, gn+ws/mask_rcnn_r50_fpn_gn_ws_20_23_24e.py, pascal_voc/ssd300_voc.py, dcn/mask_rcnn_dconv_c3-c5_r50_fpn_1x.py, dcn/cascade_rcnn_dconv_c3-c5_r50_fpn_1x.py, faster_rcnn_r101_fpn_1x.py, guided_anchoring/ga_fast_r50_caffe_fpn_1x.py, hrnet/faster_rcnn_hrnetv2p_w32_1x.py, mask_rcnn_x101_32x4d_fpn_1x.py, retinanet_r50_fpn_1x.py, gcnet/mask_rcnn_r50_fpn_sbn_1x.py, pascal_voc/ssd512_voc.py, cascade_mask_rcnn_x101_64x4d_fpn_1x.py, hrnet/mask_rcnn_hrnetv2p_w18_1x.py, empirical_attention/faster_rcnn_r50_fpn_attention_1111_1x.py, fp16/retinanet_r50_fpn_fp16_1x.py, fast_mask_rcnn_r50_fpn_1x.py, guided_anchoring/ga_rpn_x101_32x4d_fpn_1x.py, fcos/fcos_r50_caffe_fpn_gn_1x_4gpu.py, mask_rcnn_r50_caffe_c4_1x.py, empirical_attention/faster_rcnn_r50_fpn_attention_0010_dcn_1x.py, gcnet/mask_rcnn_r4_gcb_c3-c5_r50_fpn_syncbn_1x.py, rpn_r50_fpn_1x.py, ssd300_coco.py, cascade_rcnn_x101_64x4d_fpn_1x.py, htc/htc_dconv_c3-c5_mstrain_400_1400_x101_64x4d_fpn_20e-amir.py, gn/mask_rcnn_r50_fpn_gn_2x.py, rpn_r50_caffe_c4_1x.py, ms_rcnn/ms_rcnn_r50_caffe_fpn_1x.py, fast_mask_rcnn_r50_caffe_c4_1x.py, mask_rcnn_r50_fpn_1x.py, libra_rcnn/libra_retinanet_r50_fpn_1x.py, grid_rcnn/grid_rcnn_gn_head_r50_fpn_2x.py, gcnet/mask_rcnn_r16_gcb_c3-c5_r50_fpn_syncbn_1x.py, dcn/cascade_mask_rcnn_dconv_c3-c5_r50_fpn_1x.py, hrnet/faster_rcnn_hrnetv2p_w18_1x.py, fp16/faster_rcnn_r50_fpn_fp16_1x.py, cascade_rcnn_x101_32x4d_fpn_1x.py, libra_rcnn/libra_faster_rcnn_r101_fpn_1x.py, dcn/faster_rcnn_dconv_c3-c5_r50_fpn_1x.py, faster_rcnn_ohem_r50_fpn_1x.py, ssd512_coco.py, dcn/faster_rcnn_dconv_c3-c5_x101_32x4d_fpn_1x.py, gn+ws/faster_rcnn_r50_fpn_gn_ws_1x.py, fast_mask_rcnn_r101_fpn_1x.py, libra_rcnn/libra_faster_rcnn_x101_64x4d_fpn_1x.py, gn/mask_rcnn_r50_fpn_gn_contrib_2x.py, ms_rcnn/ms_rcnn_x101_64x4d_fpn_1x.py, faster_rcnn_x101_32x4d_fpn_1x.py, retinanet_r101_fpn_1x.py, faster_rcnn_r50_caffe_c4_1x.py, scratch/scratch_mask_rcnn_r50_fpn_gn_6x.py, faster_rcnn_x101_64x4d_fpn_1x.py, gn+ws/mask_rcnn_r50_fpn_gn_ws_2x.py, guided_anchoring/ga_retinanet_x101_32x4d_fpn_1x.py, retinanet_x101_64x4d_fpn_1x.py, fast_rcnn_r50_fpn_1x.py, hrnet/mask_rcnn_hrnetv2p_w32_1x.py, htc/htc_dconv_c3-c5_mstrain_400_1400_x101_64x4d_fpn_20e.py, htc/htc_r50_fpn_20e.py, fast_rcnn_r50_caffe_c4_1x.py, gn+ws/mask_rcnn_x101_32x4d_fpn_gn_ws_2x.py, wider_face/ssd300_wider_face.py, guided_anchoring/ga_retinanet_r50_caffe_fpn_1x.py, hrnet/faster_rcnn_hrnetv2p_w40_1x.py, htc/htc_r101_fpn_20e.py, rpn_x101_32x4d_fpn_1x.py, grid_rcnn/grid_rcnn_gn_head_x101_32x4d_fpn_2x.py, dcn/faster_rcnn_mdconv_c3-c5_r50_fpn_1x.py, guided_anchoring/ga_rpn_r50_caffe_fpn_1x.py
//	backbone: ResNeXt, HRNet, SSDVGG, ResNet
//	neck: HRFPN, FPN
//	rpn_head: GARPNHead, RPNHead
//	mask_head: HTCMaskHead, FCNMaskHead
//	bbox_head: SSDHead, BBoxHead, ConvFCBBoxHead, FCOSHead, RetinaHead, GARetinaHead, SharedFCBBoxHead
	

	public List<String> getFilteredValues(String paramName){
		List<String> result = Collections.emptyList();
		Stream<MMDetCfgData> configsStream = this.getRelevantConfigs(paramName);
		Stream<String> resultStream = null;
		if(paramName.equals("architecture")) {
			resultStream = configsStream.map(x->x.getArchitecture());			
		}
		else if(paramName.equals("path")) {
			resultStream = configsStream.map(x->x.getPath());			
		}
		else if(this.configKeys.contains(paramName)) {
			resultStream = configsStream.map(x->x.getParameterValue(paramName));
		}
		if(resultStream != null) {
			result = new ArrayList<>(resultStream.distinct().filter(x->x!=null).collect(Collectors.toList())); 
		}		
		return result;
	}
	
	private Stream<MMDetCfgData> getRelevantConfigs(String paramToSkip){
		return this.configs.stream().filter(x->{
			if(!paramToSkip.equals("architecture") && this.architecture != null && !this.architecture.equals(x.getArchitecture())) {
				return false;
			}
			if(!paramToSkip.equals("path") && this._selectedConfig!=null && !this._selectedConfig.getPath().equals(x.getPath())) {
				return false;
			}
			if(!paramToSkip.equals("backbone") && !isEmptyString(this.backbone) && !this.backbone.equals(x.getParameterValue("backbone"))){
				return false;
			}
			if(!paramToSkip.equals("neck") && !isEmptyString(this.neck) && !this.neck.equals(x.getParameterValue("neck"))){
				return false;
			}
			if(!paramToSkip.equals("rpn_head") && !isEmptyString(this.rpn_head) && !this.rpn_head.equals(x.getParameterValue("rpn_head"))){
				return false;
			}
			if(!paramToSkip.equals("bbox_head") && !isEmptyString(this.bbox_head) && !this.bbox_head.equals(x.getParameterValue("bbox_head"))){
				return false;
			}
			if(!paramToSkip.equals("mask_head") && !isEmptyString(this.mask_head) && !this.mask_head.equals(x.getParameterValue("mask_head"))){
				return false;
			}
			return true;
		});
	}
	
	public String finish() {
		try {
		String configName = fileName(this._selectedConfig.getPath());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(InstanceSegmentationTemplate.class.getResourceAsStream("/templates/instanceSegmentationWizard.yaml.txt")));
		Stream<String> lines = bufferedReader.lines();
		
		String result=lines.collect(Collectors.joining(System.lineSeparator()));
		bufferedReader.close();
		result=result.replace((CharSequence)"{width}", ""+this.width);
		String augmentation="";
//		if (this.hFlip) {
//			augmentation=augmentation+"   Fliplr: 0.5"+System.lineSeparator();
//		}
//		if (this.vFlip) {
//			augmentation=augmentation+"   Flipud: 0.5"+System.lineSeparator();
//		}
//		result=result.replace((CharSequence)"{aug}", ""+augmentation);
		result=result.replace((CharSequence)"{height}", ""+this.height);
		result=result.replace((CharSequence)"{numClasses}", ""+this.numClasses);
		result=result.replace((CharSequence)"{imagesPerGpu}", ""+this.imagesPerGpu);
		result=result.replace((CharSequence)"{configPath}", "configPath: ./" + configName  + ".py");
		
		String weightsStr = "";
		if(this.weightsPath != null) {
			weightsStr = "weightsPath: "+this.weightsPath;
		}
		result=result.replace((CharSequence)"{weightsPath}", weightsStr);
		
		return result; 
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
	}
	
	public void finishExperimentFolder(IFolder folder) {
		
		try {
			if (!folder.exists()) {
				folder.create(true, true, new NullProgressMonitor());
			}
			String cfgPath = this._selectedConfig.getPath();
			String configName = new Path(cfgPath).lastSegment();
			String wsSrcPath = this._selectedConfig.getWSPath();

			IFile file = folder.getFile(configName);
			InputStream contents = null;
			Path wsFolderPath = new Path(wsSrcPath);
			if(wsFolderPath.isAbsolute()) {
				File srcFile = new File(wsSrcPath);
				contents = new BufferedInputStream(new FileInputStream(srcFile));
			}
			else {
				IFolder srcFolder = file.getWorkspace().getRoot().getFolder(wsFolderPath);
				contents = srcFolder.getFile(configName).getContents();
			}
			
			
			if (file.exists()) {
				file.setContents(contents, true, true, new NullProgressMonitor());
			} else {
				file.create(contents, true, new NullProgressMonitor());
			}
		} catch (Exception e) {
			MessageDialog.openError(org.eclipse.swt.widgets.Display.getCurrent().getActiveShell(), "Error", e.getMessage());
		}
	}
	
	public void resetModel() {
		this.architecture = null;		
		this.backbone = null;
		this.neck = null;
		this.rpn_head = null;
		this.bbox_head = null;
		this.mask_head = null;
		this._selectedConfig = null;
		this.weightsPath = null;
		ObjectChangeManager.markChanged(this);
	}
	
	
	public static boolean isEmptyString(String str) {
		return str == null || str.trim().isEmpty();
	}

	public List<MMDetCfgData> getPathsList() {
		Stream<MMDetCfgData> configsStream = this.getRelevantConfigs("path");
		List<MMDetCfgData> result = configsStream.collect(Collectors.toList());
		return result;
	}

	public void setPathsList(List<MMDetCfgData> pathsList) {
		this.pathsList1 = pathsList;
	}
	
	public void findWeihtsForConfig() {
		
		this.weightsPath = null;
		for(String wpFull: this.checkpointNames) {			
			if(conforms(_selectedConfig.getPath(),wpFull)) {
				this.weightsPath = "open-mmlab://" + fileName(_selectedConfig.getPath());				
			}
		}
		ObjectChangeManager.markChanged(this);		
	}

	private static String fileName(String pth) {
		int ind1 = pth.lastIndexOf("/");
		if (ind1 < 0) {
			ind1 = 0;
		}
		else {
			ind1++;
		}
		int ind2 = pth.lastIndexOf(".");
		if(ind2<0) {
			ind2 = pth.length();
		}
		String cp = pth.substring(ind1, ind2);
		return cp;
	}
	
	public static boolean conforms(String configPath,String weihtsPath) {
		String cp = fileName(configPath); 
		String wp = fileName(weihtsPath);
		if(!wp.startsWith(cp)) {
			return false;
		}			
		boolean result = wp.startsWith(cp);
		return result;
	}	
	
	public static class WeihtsPathValidator implements IValidator<String>{

		private static final long serialVersionUID = 458834765845302551L;

		@Override
		public CodeAndMessage isValid(IValidationContext arg0, String arg1) {
			if(arg1 == null) {
				return CodeAndMessage.OK_MESSAGE;
			}
			if(!(arg0 instanceof Binding)) {
				return CodeAndMessage.OK_MESSAGE;
			}
			Object rootObj = ((Binding)arg0).getRoot().getValue();
			if(!(rootObj instanceof InstanceSegmentationTemplate)) {
				return CodeAndMessage.OK_MESSAGE;
			}
			MMDetCfgData configPath = ((InstanceSegmentationTemplate)rootObj)._selectedConfig;
			if(configPath==null || conforms(configPath.getPath(), arg1)) {
				return CodeAndMessage.OK_MESSAGE;
			}
			return CodeAndMessage.errorMessage("Selected config and weights do not correspond each other");
		}
		
	}

	@Caption("MMDetection config path")
	public MMDetCfgData getSelectedConfig() {
		return _selectedConfig;
	}

	public void setSelectedConfig(MMDetCfgData cfg) {
		this._selectedConfig = cfg;
		if(cfg!=null) {
			this.architecture = cfg.getArchitecture();
			this.backbone = cfg.getParameterValue("backbone");
			this.neck = cfg.getParameterValue("neck");
			this.rpn_head = cfg.getParameterValue("rpn_head");
			this.mask_head = cfg.getParameterValue("mask_head");
			this.bbox_head = cfg.getParameterValue("bbox_head");
			ObjectChangeManager.markChanged(this);					
		}
	}
	
	public String getBasicPath(int ind) {
		return this.basicPaths.get(ind);
	}
	
	@Caption("Faster RCNN 101")
	public void setConfig0() {
		setConfigBasicWay(0);
	}
	
	public boolean getConfig0() {
		return checkConfigBasicWay(0);
	}
	
	@Caption("Faster RCNN 50")	
	public void setConfig1() {
		setConfigBasicWay(1);
	}
	
	public boolean getConfig1() {
		return checkConfigBasicWay(1);
	}
	
	@Caption("Hybrid Task Cascade")
	public void setConfig2() {
		setConfigBasicWay(2);
	}
	
	public boolean getConfig2() {
		return checkConfigBasicWay(2);
	}
	
	@Caption("Mask RCNN")	
	public void setConfig3() {
		setConfigBasicWay(3);
	}
	
	public boolean getConfig3() {
		return checkConfigBasicWay(3);
	}
	
	private void setConfigBasicWay(int ind) {
		String url = this.basicPaths.get(ind);
		MMDetCfgData cfg = this.configsByPath.get(url);
		this.setSelectedConfig(cfg);
		this.findWeihtsForConfig();
	}
	
	private boolean checkConfigBasicWay(int ind) {
		if(this._selectedConfig==null) {
			return false;
		}
		String url = this.basicPaths.get(ind);
		return url.equals(this._selectedConfig.getPath());
	}  
}
