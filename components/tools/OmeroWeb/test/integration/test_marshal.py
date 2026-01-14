#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
# Copyright (C) 2015 University of Dundee & Open Microscopy Environment.
# All rights reserved.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

"""
Test json methods of webgateway
"""

from django.urls import reverse

from omeroweb.testlib import IWebTest, get_json


class TestImgDetail(IWebTest):

    """
    Tests json for webgateway/imgData/
    """

    def test_image_detail(self):
        """
        Marshalling of non-SPW single resolution single channel image
        """
        user_name = "%s %s" % (self.user.firstName.val, self.user.lastName.val)

        # Import image with metadata and get ImageID
        images = self.import_fake_file(
            client=self.client, pixelType="int16", sizeX=20, sizeY=20, sizeZ=5,
            sizeT=6)
        iid = images[0].id.val
        json_url = reverse('webgateway_imageData_json', args=[iid])
        data = {}
        img_data = get_json(self.django_client, json_url, data,
                            status_code=200)

        # Not a big image - tiles should be False with no other tiles metadata
        assert img_data['tiles'] is False
        assert 'levels' not in img_data
        assert 'zoomLevelScaling' not in img_data
        assert 'tile_size' not in img_data
        assert 'resolutions' not in img_data

        # Channels metadata
        assert len(img_data['channels']) == 1
        assert img_data['channels'][0] == {
            'color': "808080",
            'active': True,
            'window': {
                'max': 32767.0,
                'end': 12.0,
                'start': -32768.0,
                'min': -32768.0
            },
            'family': 'linear',
            'coefficient': 1.0,
            'reverseIntensity': False,
            'inverted': False,
            'emissionWave': None,
            'label': "0"  # to be reviewed when wavelength is supported
        }
        assert img_data['pixel_range'] == [-32768, 32767]
        assert img_data['rdefs'] == {
            'defaultT': 0,
            'model': "greyscale",
            'invertAxis': False,
            'projection': "normal",
            'defaultZ': 2
        }

        # Core image metadata
        assert img_data['size'] == {
            'width': 20,
            'c': 1,
            'z': 5,
            't': 6,
            'height': 20
        }
        assert img_data['meta']['pixelsType'] == "int16"
        assert img_data['meta']['projectName'] == "Multiple"
        assert img_data['meta']['imageId'] == iid
        assert img_data['meta']['imageAuthor'] == user_name
        assert img_data['meta']['datasetId'] is None
        assert img_data['meta']['projectDescription'] == ""
        assert img_data['meta']['datasetName'] == "Multiple"
        assert img_data['meta']['wellSampleId'] == ""
        assert img_data['meta']['projectId'] is None
        assert img_data['meta']['imageDescription'] == ""
        assert img_data['meta']['wellId'] == ""
        assert img_data['meta']['imageName'].endswith(".fake")
        assert img_data['meta']['datasetDescription'] == ""
        # Don't know exact timestamp of import
        assert 'imageTimestamp' in img_data['meta']

        # Permissions - User is owner. All perms are True
        assert img_data['perms'] == {
            'canAnnotate': True,
            'canEdit': True,
            'canDelete': True,
            'canLink': True
        }

        # Sizes for split channel image
        assert img_data['split_channel'] == {
            'c': {
                'width': 46,
                'gridy': 1,
                'border': 2,
                'gridx': 2,
                'height': 24
            },
            'g': {
                'width': 24,
                'gridy': 1,
                'border': 2,
                'gridx': 1,
                'height': 24
            }
        }

    def test_pyramidal_image(self):
        """
        Marshalling of non-SPW multi-resolution RGB image
        """
        user_name = "%s %s" % (self.user.firstName.val, self.user.lastName.val)

        # Import image with metadata and get ImageID
        images = self.import_fake_file(
            client=self.client, sizeX=20000, sizeY=20000, sizeC=3, rgb=3,
            resolutions=5)
        iid = images[0].id.val
        json_url = reverse('webgateway_imageData_json', args=[iid])
        data = {}
        img_data = get_json(self.django_client, json_url, data,
                            status_code=200)

        # Not a big image - tiles should be False with no other tiles metadata
        assert img_data['tiles'] is True
        assert img_data['levels'] == 5
        assert 'tile_size' in img_data
        assert img_data['zoomLevelScaling'] == {
            "0": 1,
            "1": 0.5,
            "2": 0.25,
            "3": 0.125,
            "4": 0.0625
        }
        assert img_data['resolutions'] == {
            "0": {
              "sizeX": 20000,
              "sizeY": 20000
            },
            "1": {
              "sizeX": 10000,
              "sizeY": 10000
            },
            "2": {
              "sizeX": 5000,
              "sizeY": 5000
            },
            "3": {
              "sizeX": 2500,
              "sizeY": 2500
            },
            "4": {
              "sizeX": 1250,
              "sizeY": 1250
            }
        }

        # Channels metadata
        assert len(img_data['channels']) == 3
        assert img_data['channels'][0] == {
            'color': "FF0000",
            'active': True,
            'window': {
                'min': 0,
                'max': 255,
                'start': 0,
                'end': 255
            },
            'family': 'linear',
            'coefficient': 1.0,
            'reverseIntensity': False,
            'inverted': False,
            'emissionWave': None,
            'label': "0"
        }
        assert img_data['channels'][1] == {
            'color': "00FF00",
            'active': True,
            'window': {
                'min': 0,
                'max': 255,
                'start': 0,
                'end': 255
            },
            'family': 'linear',
            'coefficient': 1.0,
            'reverseIntensity': False,
            'inverted': False,
            'emissionWave': None,
            'label': "1"
        }
        assert img_data['channels'][2] == {
            'color': "0000FF",
            'active': True,
            'window': {
                'min': 0,
                'max': 255,
                'start': 0,
                'end': 255
            },
            'family': 'linear',
            'coefficient': 1.0,
            'reverseIntensity': False,
            'inverted': False,
            'emissionWave': None,
            'label': "2"
        }
        assert img_data['pixel_range'] == [0, 255]
        assert img_data['rdefs'] == {
            'defaultT': 0,
            'model': "color",
            'invertAxis': False,
            'projection': "normal",
            'defaultZ': 2
        }

        # Core image metadata
        assert img_data['size'] == {
            'width': 20000,
            'height': 20000,
            'z': 1,
            't': 1,
            'c': 3
        }
        assert img_data['meta']['pixelsType'] == "uint8"
        assert img_data['meta']['projectName'] == "Multiple"
        assert img_data['meta']['imageId'] == iid
        assert img_data['meta']['imageAuthor'] == user_name
        assert img_data['meta']['datasetId'] is None
        assert img_data['meta']['projectDescription'] == ""
        assert img_data['meta']['datasetName'] == "Multiple"
        assert img_data['meta']['wellSampleId'] == ""
        assert img_data['meta']['projectId'] is None
        assert img_data['meta']['imageDescription'] == ""
        assert img_data['meta']['wellId'] == ""
        assert img_data['meta']['imageName'].endswith(".fake")
        assert img_data['meta']['datasetDescription'] == ""
        # Don't know exact timestamp of import
        assert 'imageTimestamp' in img_data['meta']

        # Permissions - User is owner. All perms are True
        assert img_data['perms'] == {
            'canAnnotate': True,
            'canEdit': True,
            'canDelete': True,
            'canLink': True
        }
