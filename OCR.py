from paddleocr import PaddleOCR
import cv2
import numpy as np
from PIL import Image
# import os
#
# os.environ['CUDA_VISIBLE_DEVICES'] = '-1'

def paddleocr(image):
    # ocr = PaddleOCR(use_gpu=False, det_model_dir='path_to_ch_PP-OCRv3_xx_model')
    ocr = PaddleOCR(lang='ch')
    result = ocr.ocr(image)
    boxes = [result[0][i][0] for i in range(len(result[0]))]
    texts = [result[0][i][-1][0] for i in range(len(result[0]))]
    return boxes, texts

def draw_ocr(image, boxes):
    image_cv = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

    # 绘制矩形框
    for box in zip(boxes):
        box = np.array(box).astype(int)
        cv2.polylines(image_cv, [box], True, (0, 255, 0), 2)

    image_pil = Image.fromarray(cv2.cvtColor(image_cv, cv2.COLOR_BGR2RGB))

    return image_pil
