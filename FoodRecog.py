import keras
import numpy as np
from keras.models import load_model
from PIL import Image
from io import BytesIO
# import os
#
# os.environ['CUDA_VISIBLE_DEVICES'] = '-1'

labels = {
    0: 'bawan',
    1: 'beef_noodles',
    2: 'beef_soup',
    3: 'bitter_melon_with_salted_eggs',
    4: 'braised_napa_cabbage',
    5: 'braised_pork_over_rice',
    6: 'brown_sugar_cake',
    7: 'bubble_tea',
    8: 'caozaiguo',
    9: 'chicken_mushroom_soup',
    10: 'chinese_pickled_cucumber',
    11: 'coffin_toast',
    12: 'cold_noodles',
    13: 'crab_migao',
    14: 'deep-fried_chicken_cutlets',
    15: 'deep_fried_pork_rib_and_radish_soup',
    16: 'dried_shredded_squid',
    17: 'egg_pancake_roll',
    18: 'eight_treasure_shaved_ice',
    19: 'fish_head_casserole',
    20: 'fried-spanish_mackerel_thick_soup',
    21: 'fried_eel_noodles',
    22: 'fried_instant_noodles',
    23: 'fried_rice_noodles',
    24: 'ginger_duck_stew',
    25: 'grilled_corn',
    26: 'grilled_taiwanese_sausage',
    27: 'hakka_stir-fried',
    28: 'hot_sour_soup',
    29: 'hung_rui_chen_sandwich',
    30: 'intestine_and_oyster_vermicelli',
    31: 'iron_egg',
    32: 'jelly_of_gravey_and_chicken_feet_skin',
    33: 'jerky',
    34: 'kung-pao_chicken',
    35: 'luwei',
    36: 'mango_shaved_ice',
    37: 'meat_dumpling_in_chili_oil',
    38: 'milkfish_belly_congee',
    39: 'mochi',
    40: 'mung_bean_smoothie_milk',
    41: 'mutton_fried_noodles',
    42: 'mutton_hot_pot',
    43: 'nabeyaki_egg_noodles',
    44: 'night_market_steak',
    45: 'nougat',
    46: 'oyster_fritter',
    47: 'oyster_omelet',
    48: 'papaya_milk',
    49: 'peanut_brittle',
    50: 'pepper_pork_bun',
    51: "pig's_blood_soup",
    52: 'pineapple_cake',
    53: 'pork_intestines_fire_pot',
    54: 'potsticker',
    55: 'preserved_egg_tofu',
    56: 'rice_dumpling',
    57: 'rice_noodles_with_squid',
    58: 'rice_with_soy-stewed_pork',
    59: 'roasted_sweet_potato',
    60: 'sailfish_stick',
    61: 'salty_fried_chicken_nuggets',
    62: 'sanxia_golden_croissants',
    63: 'saute_spring_onion_with_beef',
    64: 'scallion_pancake',
    65: 'scrambled_eggs_with_shrimp',
    66: 'scrambled_eggs_with_tomatoes',
    67: 'seafood_congee',
    68: 'sesame_oil_chicken_soup',
    69: 'shrimp_rice',
    70: 'sishen_soup',
    71: 'sliced_pork_bun',
    72: 'spicy_duck_blood',
    73: 'steam-fried_bun',
    74: 'steamed_cod_fish_with_crispy_bean',
    75: 'steamed_taro_cake',
    76: "stewed_pig's_knuckles",
    77: 'stinky_tofu',
    78: 'stir-fried_calamari_broth',
    79: 'stir-fried_duck_meat_broth',
    80: 'stir-fried_loofah_with_clam',
    81: 'stir-fried_pork_intestine_with_ginger',
    82: 'stir_fried_clams_with_basil',
    83: 'sugar_coated_sweet_potato',
    84: 'sun_cake',
    85: 'sweet_and_sour_pork_ribs',
    86: 'sweet_potato_ball',
    87: 'taiwanese_burrito',
    88: 'taiwanese_pork_ball_soup',
    89: 'taiwanese_sausage_in_rice_bun',
    90: 'tanghulu',
    91: 'tangyuan',
    92: 'taro_ball',
    93: 'three-cup_chicken',
    94: 'tube-shaped_migao',
    95: 'turkey_rice',
    96: 'turnip_cake',
    97: 'twist_roll',
    98: 'wheel_pie',
    99: 'xiaolongbao',
    100: 'yolk_pastry'
}

input_shape = (150, 150)


def read_image(image_encoded):
    pil_image = Image.open(BytesIO(image_encoded))
    return pil_image


def preprocess(image: Image.Image):
    image = image.resize(input_shape)
    image = image.convert("RGB")  # 轉換為 RGB 色彩模式
    image_array = np.array(image)  # 轉換為 NumPy 陣列
    image_array = image_array / 255.0  # 正規化圖像數據
    input_image = np.expand_dims(image_array, axis=0)  # 將圖片陣列擴展為形狀為 (1, 150, 150, 3) 的批次
    return input_image


def acc_top5(y_true, y_pred):
    return keras.metrics.top_k_categorical_accuracy(y_true, y_pred, k=5)


def load_custom_model():
    path = r'TaiwaneseFood_101.h5'
    model_load = load_model(path, custom_objects={'acc_top5': acc_top5})
    return model_load


model = load_custom_model()



def predict(image: np.array):
    prediction = model.predict(image)

    # 回傳最高機率的index
    result = np.argmax(prediction)

    # 回傳最高機率的食物名稱
    # result = labels[predicted_class_index]

    # 回傳前五名的index
    # pred_array = np.argsort(prediction)
    # top5 = pred_array[0, -5:]
    # result = [str(x) for x in top5]
    return result

# def food_pred(img):
#     path = r'TaiwaneseFood_101.h5'
#     model = load_model(path, custom_objects={'acc_top5': acc_top5})
#     # 讀取圖片並進行必要的前處理
#     image = Image.open(img)
#     image = image.resize((150, 150))  # 調整大小為模型所需的尺寸
#     image = image.convert("RGB")  # 轉換為 RGB 色彩模式
#     image_array = np.array(image)  # 轉換為 NumPy 陣列
#     image_array = image_array / 255.0  # 正規化圖像數據
#
#     input_image = np.expand_dims(image_array, axis=0)  # 將圖片陣列擴展為形狀為 (1, 150, 150, 3) 的批次
#
#     prediction = model.predict(input_image)
#     predicted_class_index = np.argmax(prediction)
#
#     result = labels[predicted_class_index]
#     return result
