package com.onpositive.datasets.visualisation.ui.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onpositive.mmdetection.wrappers.ExampleExtractor;
import com.onpositive.mmdetection.wrappers.MMDetCfgData;
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
	
	private List<MMDetCfgData> configs = null;
	
	private List<String> pathsList1;
	
	private List<String> weightPaths;
	
	private Set<String> configKeys = new HashSet<>();
	
	private Map<String,MMDetCfgData> configsByPath = new HashMap<>();
	
	{
		ExampleExtractor ee = new ExampleExtractor();
		this.configs = ee.processFile("TestProject/data/configs");
		this.weightPaths = ee.gatherWeightPaths("TestProject/data/checkpoints");
		for(MMDetCfgData cfg : this.configs) {
			this.configKeys.addAll(cfg.paramNames());
			this.configsByPath.put(cfg.getPath(), cfg);
		}
		
	}
	
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
	
	protected String configPath1;
	
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
			if(!paramToSkip.equals("path") && !isEmptyString(this.configPath1) && !this.configPath1.equals(x.getPath())) {
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
		result=result.replace((CharSequence)"{aug}", ""+augmentation);
		result=result.replace((CharSequence)"{height}", ""+this.height);
		result=result.replace((CharSequence)"{numClasses}", ""+this.numClasses);
		result=result.replace((CharSequence)"{activation}", ""+this.activation);
		result=result.replace((CharSequence)"{architecture}", ""+this.architecture);
		result=result.replace((CharSequence)"{backbone}", ""+this.backbone);
				
		return result; 
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
	}
	
	public void resetModel() {
		this.architecture = null;		
		this.backbone = null;
		this.neck = null;
		this.rpn_head = null;
		this.bbox_head = null;
		this.mask_head = null;
		this.configPath1 = null;
		this.weightsPath = null;
		ObjectChangeManager.markChanged(this);
	}
	
	
	public static boolean isEmptyString(String str) {
		return str == null || str.trim().isEmpty();
	}

	public List<String> getPathsList() {
		return getFilteredValues("path");
	}

	public void setPathsList(List<String> pathsList) {
		this.pathsList1 = pathsList;
	}
	
	public void findWeihtsForConfig() {
		if(this.configPath1 == null) {
			return;
		}
		String cp = fileName(configPath1);
		for(String wpFull: this.weightPaths) {			
			if(conforms(configPath1,wpFull)) {
				this.weightsPath = wpFull;				
			}
		}
		ObjectChangeManager.markChanged(this);		
	}

	private static String fileName(String pth) {
		int ind = pth.lastIndexOf("/");
		if (ind < 0) {
			ind = 0;
		}
		else {
			ind++;
		}
		String cp = pth.substring(ind, pth.lastIndexOf("."));
		return cp;
	}
	
	public static boolean conforms(String configPath,String weihtsPath) {
		String cp = fileName(configPath); 
		String wp = fileName(weihtsPath);
		if(!wp.startsWith(cp)) {
			return false;
		}			
		String postfix = wp.substring(cp.length());
		String ggg = postfix.replaceAll("[_\\-0-9a-f]", "");
		boolean result = ggg.isEmpty();
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
			String configPath = ((InstanceSegmentationTemplate)rootObj).configPath1;
			if(configPath==null || conforms(configPath, arg1)) {
				return CodeAndMessage.OK_MESSAGE;
			}
			return CodeAndMessage.errorMessage("Selected config and weights do not correspond each other");
		}
		
	}

	@Caption("MMDetection config path")
	public String getConfigPath() {
		return configPath1;
	}

	public void setConfigPath(String configPath) {
		this.configPath1 = configPath;
		MMDetCfgData cfg = this.configsByPath.get(configPath);
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
}
