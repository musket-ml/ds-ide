from musket_core import inference
import os

@inference.inference_service_factory
def createEngine():
    return inference.BasicEngine(os.path.join(os.path.dirname(__file__),"config.yaml"),["comment_text"],["target"],{"comment_text":"as_is","target":"binary"})